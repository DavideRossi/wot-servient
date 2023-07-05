/*
 * Copyright (c) 2019-2022 Heiko Bornholdt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */
package city.sane.wot.thing;

import city.sane.ObjectBuilder;
import city.sane.wot.thing.form.Form;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract representation of a Thing Interaction (inherited from {@link
 * city.sane.wot.thing.action.ThingAction}, {@link city.sane.wot.thing.event.ThingEvent} and {@link
 * city.sane.wot.thing.property.ThingProperty})
 *
 * @param <T>
 */
public abstract class ThingInteraction<T> {
    private static final Logger log = LoggerFactory.getLogger(ThingInteraction.class);

    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected String description;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected Map<String, String> descriptions;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected List<Form> forms = new ArrayList<>();
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected Map<String, Map<String, Object>> uriVariables = new HashMap<>();

    public String getDescription() {
        return description;
    }

    public Map<String, String> getDescriptions() {
        return descriptions;
    }

    public List<Form> getForms() {
        return forms;
    }

    public Map<String, Map<String, Object>> getUriVariables() {
        return uriVariables;
    }

    public T setForms(List<Form> forms) {
        this.forms = forms;
        return (T) this;
    }

    public T addForm(Form form) {
        forms.add(form);
        return (T) this;
    }
    
    protected List<Form> normalizeHrefs(List<Form> forms, ConsumedThing thing) {
        return forms.stream().map(f -> normalizeHref(f, thing)).collect(Collectors.toList());
    }
    
    protected Form normalizeHref(Form form, ConsumedThing thing) {
        String base = thing.getBase();
        if (base != null && !base.isEmpty() && !form.getHref().matches("^(?i:[a-z+]+:).*")) {
            String normalizedHref = base + form.getHref();
            return new Form.Builder(form).setHref(normalizedHref).build();
        }
        else {
            return form;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, descriptions, forms, uriVariables);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ThingInteraction)) {
            return false;
        }
        ThingInteraction<?> that = (ThingInteraction<?>) o;
        return Objects.equals(description, that.description) &&
                Objects.equals(descriptions, that.descriptions) &&
                Objects.equals(forms, that.forms) &&
                Objects.equals(uriVariables, that.uriVariables);
    }

    public abstract static class AbstractBuilder<T extends ObjectBuilder> implements ObjectBuilder {
        String description;
        Map<String, String> descriptions;
        List<Form> forms = new ArrayList<>();
        Map<String, Map<String, Object>> uriVariables = new HashMap<>();

        public T setDescription(String description) {
            this.description = description;
            return (T) this;
        }

        public T setDescription(Map<String, String> descriptions) {
            this.descriptions = descriptions;
            return (T) this;
        }

        public T setForms(List<Form> forms) {
            this.forms = forms;
            return (T) this;
        }

        public T addForm(Form form) {
            forms.add(form);
            return (T) this;
        }

        public T setUriVariables(Map<String, Map<String, Object>> uriVariables) {
            this.uriVariables = uriVariables;
            return (T) this;
        }

        protected void applyInteractionParameters(ThingInteraction interaction) {
            interaction.description = description;
            interaction.descriptions = descriptions;
            interaction.forms = forms;
            interaction.uriVariables = uriVariables;
        }
    }
}
