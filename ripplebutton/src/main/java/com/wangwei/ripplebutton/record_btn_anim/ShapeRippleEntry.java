/*
 * Copyright 2016 Rodolfo Navalon.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wangwei.ripplebutton.record_btn_anim;


public class ShapeRippleEntry {

    private BaseShapeRipple baseShapeRipple;

    private boolean isRender;

    private float radiusSize;

    private float fractionValue;

    private int rippleIndex;

    private int x;

    private int y;

    private int originalColorValue;

    private int changingColorValue;

    public ShapeRippleEntry() {
    }

    public ShapeRippleEntry(BaseShapeRipple baseShapeRipple) {
        this.baseShapeRipple = baseShapeRipple;
    }

    public BaseShapeRipple getBaseShapeRipple() {
        return baseShapeRipple;
    }

    public void setBaseShapeRipple(BaseShapeRipple baseShapeRipple) {
        this.baseShapeRipple = baseShapeRipple;
    }

    public float getRadiusSize() {
        return radiusSize;
    }

    public void setRadiusSize(float radiusSize) {
        this.radiusSize = radiusSize;
    }

    public int getOriginalColorValue() {
        return originalColorValue;
    }

    public void setOriginalColorValue(int originalColorValue) {
        this.originalColorValue = originalColorValue;
        setChangingColorValue(originalColorValue);
    }

    public float getFractionValue() {
        return fractionValue;
    }

    public void setFractionValue(float fractionValue) {
        this.fractionValue = fractionValue;
    }

    public boolean isRender() {
        return isRender;
    }

    public void setRender(boolean render) {
        isRender = render;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getY() {
        return y;
    }

    public int getChangingColorValue() {
        return changingColorValue;
    }

    public void setChangingColorValue(int changingColorValue) {
        this.changingColorValue = changingColorValue;
    }

    public int getRippleIndex() {
        return rippleIndex;
    }

    public void setRippleIndex(int rippleIndex) {
        this.rippleIndex = rippleIndex;
    }

    public void reset() {
        isRender = false;
        fractionValue = 0f;
        radiusSize = 0;
        originalColorValue = 0;
    }
}
