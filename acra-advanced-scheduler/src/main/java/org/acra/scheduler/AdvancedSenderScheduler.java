/*
 * Copyright (c) 2018
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.acra.scheduler;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.OneTimeWorkRequest;
import com.faendir.asl.annotation.AutoService;
import org.acra.config.ConfigUtils;
import org.acra.config.CoreConfiguration;
import org.acra.config.SchedulerConfiguration;
import org.acra.plugins.HasConfigPlugin;

/**
 * Utilizes evernotes android-job to delay report sending
 *
 * @author F43nd1r
 * @since 18.04.18
 */
public class AdvancedSenderScheduler extends DefaultSenderScheduler {
    private final SchedulerConfiguration schedulerConfiguration;

    private AdvancedSenderScheduler(@NonNull Context context, @NonNull CoreConfiguration config) {
        super(context, config);
        schedulerConfiguration = ConfigUtils.getPluginConfiguration(config, SchedulerConfiguration.class);
    }

    @Override
    protected void configureWorkRequest(@NonNull OneTimeWorkRequest.Builder builder) {
        Constraints.Builder constraints = new Constraints.Builder()
                .setRequiredNetworkType(schedulerConfiguration.requiresNetworkType())
                .setRequiresCharging(schedulerConfiguration.requiresCharging())
                .setRequiresBatteryNotLow(schedulerConfiguration.requiresBatteryNotLow());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            constraints.setRequiresDeviceIdle(schedulerConfiguration.requiresDeviceIdle());
        }
        builder.setConstraints(constraints.build());
    }

    @AutoService(SenderSchedulerFactory.class)
    public static class Factory extends HasConfigPlugin implements SenderSchedulerFactory {

        public Factory() {
            super(SchedulerConfiguration.class);
        }

        @NonNull
        @Override
        public SenderScheduler create(@NonNull Context context, @NonNull CoreConfiguration config) {
            return new AdvancedSenderScheduler(context, config);
        }

    }

}
