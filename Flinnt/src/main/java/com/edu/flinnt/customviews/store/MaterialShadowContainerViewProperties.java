package com.edu.flinnt.customviews.store;

import android.util.Property;

public class MaterialShadowContainerViewProperties {

    /**
     * A Property wrapper around the <code>shadowTranslationZ</code> functionality handled by the
     * {@link MaterialShadowContainerView#setShadowTranslationZ(float)} and
     * {@link MaterialShadowContainerView#getShadowTranslationZ()} methods.
     */
    public static final Property<MaterialShadowContainerView, Float> SHADOW_TRANSLATION_Z = new Property<MaterialShadowContainerView, Float>(Float.class, "shadowTranslationZ") {
        @Override
        public void set(MaterialShadowContainerView object, Float value) {
            object.setShadowTranslationZ(value);
        }

        @Override
        public Float get(MaterialShadowContainerView object) {
            return object.getShadowTranslationZ();
        }
    };
}
