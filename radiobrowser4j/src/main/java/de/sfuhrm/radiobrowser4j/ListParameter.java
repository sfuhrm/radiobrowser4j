/*
* Copyright 2017 Stephan Fuhrmann
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
package de.sfuhrm.radiobrowser4j;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/** Parameters for list calls.
 * @author Stephan Fuhrmann
 * */
@Getter
@Slf4j
@ToString
public final class ListParameter extends ParameterProvider {
    /** The field name to sort by. */
    private FieldName order;

    /** Whether to sort in reverse order. */
    private Boolean reverseOrder;

    /** Private constructor.
     * @see #create()
     * */
    private ListParameter() {
    }

    /** Creates a new instance.
     * @return a new instance.
     * */
    public static ListParameter create() {
        return new ListParameter();
    }

    /** Order by the given field name.
     * @param fieldName the field name to order by.
     * @return {@code this} instance.
     * */
    public ListParameter order(@NonNull final FieldName fieldName) {
        this.order = fieldName;
        return this;
    }

    /** Switch to reverse order.
     * @param reverse whether to order reverse.
     * @return {@code this} instance.
     * */
    public ListParameter reverseOrder(final boolean reverse) {
        this.reverseOrder = reverse;
        return this;
    }

    /**
     * Transfer this list parameter to the passed multi-valued-map.
     * @param requestParams the target of the list params.
     * */
    protected void apply(final Map<String, String> requestParams) {
        log.info("list={}", this);
        if (getOrder() != null) {
            requestParams.put("order",
                    getOrder().name().toLowerCase());
        }
        if (getReverseOrder() != null) {
            requestParams.put("reverse",
                    getReverseOrder().toString());
        }
    }
}
