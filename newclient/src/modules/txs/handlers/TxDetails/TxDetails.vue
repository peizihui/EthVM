<template>
    <!--
    =====================================================================================
      TX DETAILS LIST
    =====================================================================================
    -->
    <v-layout row wrap justify-start class="mb-4">
        <v-flex xs12>
            <app-details-list :title="title" :details="txDetails" :is-loading="isLoading" :max-items="7" />
        </v-flex>
    </v-layout>
</template>

<script lang="ts">
import AppDetailsList from '@app/core/components/ui/AppDetailsList.vue'
import { Detail } from '@app/core/components/props'
import { Mixins, Component, Prop } from 'vue-property-decorator'
import { getTransactionByHash } from './txDetails.graphql'
import { TxDetails as TxDetailsType } from './apolloTypes/TxDetails'
import { NumberFormatMixin } from '@app/core/components/mixins/number-format.mixin'
import { FormattedNumber } from '@app/core/helper/number-format-helper'
import BN from 'bignumber.js'
import { ErrorMessageTx } from '@app/modules/txs/models/ErrorMessagesForTx'
@Component({
    components: {
        AppDetailsList
    },
    apollo: {
        transaction: {
            query: getTransactionByHash,
            variables() {
                return { hash: this.txRef }
            },
            fetchPolicy: 'cache-and-network',
            update: data => data.getTransactionByHash,
            result({ data }) {
                if (data && data.getTransactionByHash) {
                    this.emitErrorState(false)
                }
            },
            error(error) {
                const newError = JSON.stringify(error.message)
                if (newError.includes('Cannot return null for non-nullable field Query.getTransactionByHash')) {
                    this.emitErrorState(true, true)
                } else {
                    this.emitErrorState(true)
                }
            }
        }
    }
})
export default class TxDetails extends Mixins(NumberFormatMixin) {
    /*
  ===================================================================================
    Props
  ===================================================================================
  */

    @Prop({ type: String }) txRef!: string

    /*
  ===================================================================================
    Initial Data
  ===================================================================================
  */

    hasError = false
    transaction!: TxDetailsType

    /*
  ===================================================================================
    Computed Values
  ===================================================================================
  */

    /**
     * Create properly-formatted title from tokenDetails
     *
     * @return {String} - Title for details list
     */
    get title(): string {
        return this.$i18n.t('tx.detail').toString()
    }

    /*
 ===================================================================================
   Methods
 ===================================================================================
 */
    emitErrorState(val: boolean, hashNotFound = false): void {
        this.hasError = val
        const mess = hashNotFound ? ErrorMessageTx.notFound : ErrorMessageTx.details
        this.$emit('errorDetails', this.hasError, mess)
    }

    /**
     * Return properly-formatted Detail for "to" row in list component.
     *
     * @return {Detail}
     */
    toDetail(transaction: TxDetailsType): Detail {
        return {
            title: this.$i18n.t('tx.to').toString(),
            detail: transaction.to!,
            copy: true,
            link: `/address/${transaction.to!}`,
            mono: true
        }
    }

    /**
     * Properly format the Details[] array for the details table.
     * If the data hasn't been loaded yet, then only include the titles in the details.
     */
    get txDetails(): Detail[] {
        let details: Detail[]
        if (this.isLoading) {
            details = [
                {
                    title: this.$i18n.t('block.number')
                },
                {
                    title: this.$i18n.t('common.hash')
                },
                {
                    title: this.$i18n.t('common.timestmp')
                },
                {
                    title: this.$i18n.t('tx.from')
                },
                {
                    title: this.$i18n.t('common.amount')
                },
                {
                    title: this.$i18n.t('tx.to').toString()
                },
                {
                    title: this.$i18n.t('gas.limit')
                },
                {
                    title: this.$i18n.t('gas.used')
                },
                {
                    title: this.$i18n.t('gas.price')
                },
                {
                    title: this.$i18n.tc('tx.fee', 1)
                }
            ]
        } else {
            // const receipt = transaction.receipt!
            details = [
                {
                    title: this.$i18n.t('block.number'),
                    detail: this.formatNumber(this.transaction.blockNumber || 0),
                    link: `/block/number/${this.transaction.blockNumber}`
                },
                {
                    title: this.$i18n.t('common.hash'),
                    detail: this.transaction.hash,
                    copy: true,
                    mono: true
                },
                {
                    title: this.$i18n.t('common.timestmp'),
                    detail: this.transaction.timestamp !== null ? new Date(this.transaction.timestamp * 1e3).toString() : ''
                },
                {
                    title: this.$i18n.t('tx.from'),
                    detail: this.transaction.from,
                    copy: true,
                    link: `/address/${this.transaction.from}`,
                    mono: true
                },
                {
                    title: this.$i18n.t('common.amount'),
                    detail: `${this.txAmount.value} ${this.txAmount.unit}`,
                    tooltip: this.txAmount.tooltipText ? `${this.txAmount.tooltipText} ${this.$i18n.t('common.eth')}` : undefined
                },
                {
                    title: this.$i18n.t('tx.status'),
                    detail: `${this.$i18n.tc('tx.' + this.txStatus, 1)}`
                },
                // TODO need tx fee or do we calculate it ourselves ?
                {
                    title: this.$i18n.tc('tx.fee', 2),
                    detail: `${this.txFee.value} ${this.$i18n.t('common.eth')}`,
                    tooltip: this.txFee.tooltipText ? `${this.txFee.tooltipText} ${this.$i18n.t('common.eth')}` : undefined
                },
                {
                    title: this.$i18n.t('gas.limit'),
                    detail: this.formatNumber(new BN(this.transaction.gas).toNumber())
                    // tooltip: this.transaction.gasFormatted.tooltipText ? `${this.transaction.gasFormatted.tooltipText}` : undefined
                },
                {
                    title: this.$i18n.t('gas.used'),
                    detail: this.formatNumber(new BN(this.transaction.gasUsed || 0).toNumber()) // TODO genesis block txs can have no receipt
                    // tooltip: receipt && receipt.gasUsedFormatted.tooltipText ? `${receipt.gasUsedFormatted.tooltipText}` : undefined
                },
                {
                    title: this.$i18n.t('gas.price'),
                    detail: `${this.gasPrice.value} ${this.gasPrice.unit}`,
                    tooltip: this.gasPrice.tooltipText ? `${this.gasPrice.tooltipText} ${this.$i18n.t('common.eth')}` : undefined
                },
                {
                    title: this.$i18n.t('common.nonce'),
                    detail: this.transaction.nonce
                    // tooltip: this.transaction.nonce.tooltipText ? `${this.transaction.nonce.tooltipText}` : undefined
                },
                {
                    title: this.$i18n.t('tx.input'),
                    detail: this.transaction.input
                    // txInput: this.inputFormatted
                }
            ]
            if (this.transaction.to) {
                details.splice(5, 0, this.toDetail(this.transaction))
            }
        }

        return details
    }

    /**
     * Determines whether or not the tx object has been loaded/populated.
     *
     * @return {Boolean}
     */
    get isLoading(): boolean | undefined {
        return this.$apollo.queries.transaction.loading || this.hasError
    }
    /**
     * Formats the transaction value to ETH.
     *
     * @return {FormattedNumber}
     */
    get txAmount(): FormattedNumber {
        return this.formatVariableUnitEthValue(new BN(this.transaction.value))
    }
    /**
     * Formats the gas price value to gwei.
     *
     * @return {FormattedNumber}
     */
    get gasPrice(): FormattedNumber {
        return this.formatVariableUnitEthValue(new BN(this.transaction.gasPrice))
    }
    /**
     * Gets the tx status.
     *
     * @return {String}
     */
    get txStatus(): string {
        const statuses = ['0x0', '0x1']
        if (this.transaction.status === statuses[0]) {
            return 'failed'
        }
        if (this.transaction.status === statuses[1]) {
            return 'success'
        }
        return 'pending'
    }
    /**
     * Calculate the transaction fee.
     *
     * @return {FormattedNumber}
     */
    get txFee(): FormattedNumber {
        if (this.transaction && this.transaction.gasUsed) {
            const price = new BN(this.transaction.gasPrice)
            const used = new BN(this.transaction.gasUsed)
            const fee = price.times(used)
            return this.formatVariableUnitEthValue(fee)
        }
        return { value: '0' }
    }

    // TODO Figure out if we stil need this
    // get inputFormatted(): string[] {
    //     if (!this.transaction) {
    //         return ['0x']
    //     }

    //     const methodId = this.transaction.input
    //     if (!methodId) {
    //         return ['0x']
    //     }

    //     const methodFormatted = `${this.$i18n.t('tx.method')}: ${methodId}`

    //     const inputFunction = this.transaction.inputFunction

    //     if (inputFunction) {
    //         return [`${this.$i18n.t('tx.func')}: ${inputFunction}`, methodFormatted]
    //     }

    //     return [methodFormatted]
    // }
}
</script>
