package com.example.rentmanagementsystem

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.Calendar

class RentReminderWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    override fun doWork(): Result {
        val today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        val tenants = ReportsRepository(applicationContext).getOutstanding()

        for (t in tenants) {
            val daysLeft = t.dueDay - today
            when {
                daysLeft in 0..3 -> NotificationHelper.show(
                    applicationContext, (t.id * 10).toInt(),
                    "Rent due soon",
                    "${t.name} owes %.2f, due in $daysLeft day(s).".format(t.balance)
                )
                daysLeft < 0 -> NotificationHelper.show(
                    applicationContext, (t.id * 10 + 1).toInt(),
                    "Rent overdue",
                    "${t.name} is overdue by ${-daysLeft} day(s): %.2f unpaid.".format(t.balance)
                )
            }
        }
        return Result.success()
    }
}