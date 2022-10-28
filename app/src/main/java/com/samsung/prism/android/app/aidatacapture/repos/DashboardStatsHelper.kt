package com.samsung.prism.android.app.aidatacapture.repos

import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry

class DashboardStatsHelper {
    private var dataForAdminChart: MutableList<DataEntry> = ArrayList()
    private var dataForAdminValues: MutableMap<String, String> = mutableMapOf()
    private var dataForAdminAudioChart: MutableList<DataEntry> = ArrayList()
    private var dataForAdminAudioValues: MutableMap<String, String> = mutableMapOf()

    fun setStatsForAdminChartView(key:String,value:Int): Boolean {
        dataForAdminChart.add(ValueDataEntry(key,value))
        return true
    }

    fun getStatsForAdminChartView():MutableList<DataEntry>{
        return dataForAdminChart
    }

    fun setDataForAdminValues(key:String,value:String): Boolean {
        dataForAdminValues.put(key,value)
        return true
    }

    fun getDataForAdminAudioValues():MutableMap<String,String>{
        return dataForAdminAudioValues
    }

    fun setStatsForAdminAudioChartView(key:String,value:Int): Boolean {
        dataForAdminAudioChart.add(ValueDataEntry(key,value))
        return true
    }

    fun getStatsForAdminAudioChartView():MutableList<DataEntry>{
        return dataForAdminAudioChart
    }

    fun setDataForAdminAudioValues(key:String,value:String): Boolean {
        dataForAdminAudioValues.put(key,value)
        return true
    }

    @JvmName("getDataForAdminValues1")
    fun getDataForAdminValues():MutableMap<String,String>{
        return dataForAdminValues
    }
}