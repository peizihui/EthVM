<template>
    <v-app class="content">
        <the-navigation-drawer />
        <v-content>
            <v-layout column fill-height>
                <app-greeting v-if="appGreet" :greet="appGreet" />
                <v-flex>
                    <router-view :key="$route.path" />
                </v-flex>
                <the-footer />
                <app-message :connected="connected" :syncing="syncing" />
            </v-layout>
        </v-content>
    </v-app>
</template>

<script lang="ts">
import AppGreeting from '@app/core/components/ui/AppGreeting.vue'
import AppMessage from '@app/core/components/ui/AppMessage.vue'
import TheNavigationDrawer from '@app/core/components/layout/TheNavigationDrawer.vue'
import TheFooter from '@app/core/components/layout/TheFooter.vue'
import storePack from 'store'
import { Vue, Component } from 'vue-property-decorator'

const MAX_ITEMS = 10

@Component({
    components: {
        AppGreeting,
        AppMessage,
        TheNavigationDrawer,
        TheFooter
    }
})
export default class App extends Vue {
    /*
  ===================================================================================
    Initial Data
  ===================================================================================
  */

    syncing?: boolean = false

    /*
  ===================================================================================
    Computed values
  ===================================================================================
  */
    get appGreet() {
        return !storePack.get('notFirstTimeVisit')
    }
    get connected(): boolean {
        // this.$CD.getEthereumTokens().then(console.log)
        // this.$CD.getEthereumTokenByContract('0x7d1afa7b718fb893db30a3abc0cfc608aacfebb0').then(console.log)
        // this.$CD.getEthereumTokenByContract('0x7d1afa7b718fb893db30a3abc0cfc608aacfebb1').then(console.log)
        // return !this.connectedStates.has(this.state)
        return true
    }
}
</script>

<style scoped lang="css">
@import '~cssPath/global.css';

.content {
    background: #f3f4f8;
}
</style>
