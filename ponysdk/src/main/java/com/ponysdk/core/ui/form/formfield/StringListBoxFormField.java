/*
 * Copyright (c) 2011 PonySDK
 *  Owners:
 *  Luciano Broussal  <luciano.broussal AT gmail.com>
 *  Mathieu Barbier   <mathieu.barbier AT gmail.com>
 *  Nicolas Ciaravola <nicolas.ciaravola.pro AT gmail.com>
 *
 *  WebSite:
 *  http://code.google.com/p/pony-sdk/
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.ponysdk.core.ui.form.formfield;

import com.ponysdk.core.ui.basic.PListBox;
import com.ponysdk.core.ui.form.dataconverter.DataConverter;
import com.ponysdk.core.ui.form.dataconverter.IdentityConverter;

public class StringListBoxFormField extends ListBoxFormField<String> {

    public StringListBoxFormField() {
        this(new PListBox(), IdentityConverter.STRING);
    }

    public StringListBoxFormField(final PListBox listBox) {
        this(listBox, IdentityConverter.STRING);
    }

    public StringListBoxFormField(final PListBox listBox, final DataConverter<String, String> dataProvider) {
        super(listBox, dataProvider);
    }

}
