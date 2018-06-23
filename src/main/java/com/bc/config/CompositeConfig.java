/*
 * Copyright 2017 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.looseboxes.com/legal/licenses/software.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bc.config;

import java.io.IOException;
import java.util.Collection;
import java.util.Properties;

/**
 * Presents a combined view of multiple {@link com.bc.config.Config Config}s. 
 * <b>Note:</b> A property must <b>NOT</b> be repeated across the multiple
 * {@link com.bc.config.Config Config}s constituting this object.
 * The combined {@link com.bc.config.Config Config} propagates changes to the 
 * specific {@link com.bc.config.Config Config} containing the property being
 * set by the change.
 * <p>
 * <b>Note:</b>
 * The {@link #getData()} method returns a copy combining the properties 
 * from all the constituting {@link com.bc.config.Config Config}s. Hence changes 
 * to the properties returned by that method will not be reflected in the actual 
 * {@link com.bc.config.Config Config} which contains the data.
 * </p>
 * @author Chinomso Bassey Ikwuagwu on Feb 25, 2017 10:48:14 AM
 */
public class CompositeConfig extends AbstractCompositeConfig<Properties> {

    public CompositeConfig(ConfigService<Properties> configService) throws IOException {
        super(configService);
    }

    public CompositeConfig(ConfigService<Properties> configService, boolean allowDuplicates) throws IOException {
        super(configService, allowDuplicates);
    }

    public CompositeConfig(Collection<Config<Properties>> configs) {
        super(configs);
    }

    public CompositeConfig(Collection<Config<Properties>> configs, boolean allowDuplicates) {
        super(configs, allowDuplicates);
    }

    public CompositeConfig(Collection<Config<Properties>> configs, String timePattern) {
        super(configs, timePattern);
    }

    public CompositeConfig(Collection<Config<Properties>> configs, String timePattern, boolean allowDuplicates) {
        super(configs, timePattern, allowDuplicates);
    }

    /**
     * <b>Returns a copy</b>
     * @return A copy of the combination of all the {@link java.util.Properties Properties} 
     * which composes this config's data. Changes to the returned {@link java.util.Properties Properties}
     * are <b>NOT</b> reflected in this config.
     */
    @Override
    public Properties getData() {
        Properties all = new Properties();
        for(Config<Properties> config : this.getPropertiesList()) {
            for(String key : config.getNames()) {
                all.setProperty(key, config.get(key));
            }
        }
        return all;
    }
}
