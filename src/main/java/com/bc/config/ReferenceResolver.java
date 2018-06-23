/*
 * Copyright 2018 NUROX Ltd.
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

import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * @author Chinomso Bassey Ikwuagwu on May 11, 2018 4:18:21 PM
 */
public interface ReferenceResolver extends Function<String, String> {

    @Override
    default String apply(String input) {
       return this.resolve(input);
    }
    
    UnaryOperator<String> getContext();

    Pattern getReferencePattern();
    
    String resolve(String input);
    
    String resolve(UnaryOperator<String> context, String input);
}
