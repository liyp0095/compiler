/*
 * Copyright 2018, Robert Dyer, Mohd Arafat
 *                 and Bowling Green State University
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
package boa.graphs.ddg;

import boa.types.Control;

/**
 * Data Dependence Graph builder edge
 *
 * @author marafat
 */

public class DDGEdge {

    private DDGNode src;
    private DDGNode dest;
    private String label = "."; // name of the variable for def and use

    public DDGEdge(DDGNode src, DDGNode dest, String label) {
        this.src = src;
        this.dest = dest;
        this.label = label;
    }

    public DDGEdge(DDGNode src, DDGNode dest) {
        this.src = src;
        this.dest = dest;
    }

    // Setters
    public void setSrc(final DDGNode src) {
        this.src = src;
    }

    public void setDest(final DDGNode dest) {
        this.dest = dest;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    // Getters
    public DDGNode getSrc() {
        return src;
    }

    public DDGNode getDest() {
        return dest;
    }

    public String getLabel() {
        return label;
    }

    /**
     * DDG Edge builder
     *
     * @return
     */
    public boa.types.Control.DDGEdge.Builder newBuilder() {
        final boa.types.Control.DDGEdge.Builder eb = boa.types.Control.DDGEdge.newBuilder();
        eb.setLabel(DDGEdge.getLabel(this.label));
        return eb;
    }

    /**
     * Gives back label type
     *
     * @param label edge label
     * @return label type
     */
    public static Control.DDGEdge.DDGEdgeLabel getLabel(final String label) {
        if (!label.equals("."))
            return Control.DDGEdge.DDGEdgeLabel.VARDEF;
        else
            return Control.DDGEdge.DDGEdgeLabel.NIL;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DDGEdge ddgEdge = (DDGEdge) o;

        if (!src.equals(ddgEdge.src)) return false;
        if (!dest.equals(ddgEdge.dest)) return false;
        return label.equals(ddgEdge.label);
    }

    @Override
    public int hashCode() {
        int result = src.hashCode();
        result = 31 * result + dest.hashCode();
        result = 31 * result + label.hashCode();
        return result;
    }
}