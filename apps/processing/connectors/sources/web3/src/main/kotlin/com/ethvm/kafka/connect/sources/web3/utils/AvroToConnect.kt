package com.ethvm.kafka.connect.sources.web3.utils

import com.ethvm.avro.capture.BlockHeaderRecord
import com.ethvm.avro.capture.CanonicalKeyRecord
import com.ethvm.avro.capture.CanonicalRecord
import com.ethvm.avro.capture.ContractMetadataRecord
import com.ethvm.avro.capture.TraceListRecord
import com.ethvm.avro.capture.TransactionListRecord
import com.ethvm.avro.capture.TransactionReceiptListRecord
import com.ethvm.avro.capture.TransactionReceiptRecord
import com.ethvm.avro.capture.TransactionRecord
import com.ethvm.avro.capture.UncleListRecord
import com.ethvm.avro.capture.UncleRecord
import io.confluent.connect.avro.AvroData
import mu.KotlinLogging
import org.apache.avro.Schema
import org.apache.avro.specific.SpecificRecordBase
import org.apache.kafka.connect.data.SchemaAndValue

object AvroToConnect {

  private val avroData = AvroData(100)

  private val mappings = mapOf(
    BlockHeaderRecord::class to BlockHeaderRecord.`SCHEMA$`,
    CanonicalKeyRecord::class to CanonicalKeyRecord.`SCHEMA$`,
    CanonicalRecord::class to CanonicalRecord.`SCHEMA$`,
    ContractMetadataRecord::class to ContractMetadataRecord.`SCHEMA$`,
    TraceListRecord::class to TraceListRecord.`SCHEMA$`,
    TransactionListRecord::class to TransactionListRecord.`SCHEMA$`,
    TransactionReceiptListRecord::class to TransactionReceiptListRecord.`SCHEMA$`,
    TransactionRecord::class to TransactionRecord.`SCHEMA$`,
    TransactionReceiptRecord::class to TransactionReceiptRecord.`SCHEMA$`,
    UncleListRecord::class to UncleListRecord.`SCHEMA$`,
    UncleRecord::class to UncleRecord.`SCHEMA$`
  )

  fun toConnectData(record: SpecificRecordBase): SchemaAndValue = avroData.toConnectData(mappings[record::class], record)

  fun toConnectSchema(schema: Schema) = avroData.toConnectSchema(schema)
}
