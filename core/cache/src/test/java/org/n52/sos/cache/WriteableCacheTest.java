/*
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * License version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 */
package org.n52.sos.cache;

import org.junit.Before;
import org.junit.Test;


public class WriteableCacheTest {

    InMemoryCacheImpl cache;

    String feature = "feature";

    String featureOther = "featureOther";

    String featureName = "featureName";

    String featureNameOther = "featureNameOther";

    String procedure = "procedure";

    String procedureOther = "procedureOther";

    String procedureName = "procedureName";

    String procedureNameOther = "procedureNameOther";

    String observedProperty = "observedProperty";

    String observedPropertyOther = "observedPropertyOther";

    String observedPropertyName = "observedPropertyName";

    String observedPropertyNameOther = "observedPropertyNameOther";

    String offering = "offering";

    String offeringOther = "offeringOther";

    String offeringName = "offeringName";

    String offeringNameOther = "offeringNameOther";

    @Before
    public void init() {
        cache = new InMemoryCacheImpl();
        cache.addFeatureOfInterestIdentifierHumanReadableName(feature, featureName);
        cache.addProcedureIdentifierHumanReadableName(procedure, procedureName);
        cache.addObservablePropertyIdentifierHumanReadableName(observedProperty, observedPropertyName);
        cache.addOfferingIdentifierHumanReadableName(offering, offeringName);

    }

    @Test
    public void test_same_identifier_name_feature() {
        cache.addFeatureOfInterestIdentifierHumanReadableName(feature, featureName);
    }

    @Test
    public void test_same_identifier_other_name_feature() {
        cache.addFeatureOfInterestIdentifierHumanReadableName(feature, featureNameOther);
    }

    @Test
    public void test_other_identifier_same_name_feature() {
        cache.addFeatureOfInterestIdentifierHumanReadableName(featureOther, featureName);
    }

    @Test
    public void test_other_identifier_name_feature() {
        cache.addFeatureOfInterestIdentifierHumanReadableName(featureOther, featureNameOther);
    }

    @Test
    public void test_same_identifier_name_procedure() {
        cache.addProcedureIdentifierHumanReadableName(procedure, procedureName);
    }

    @Test
    public void test_same_identifier_other_name_procedure() {
        cache.addProcedureIdentifierHumanReadableName(procedure, procedureNameOther);
    }

    @Test
    public void test_other_identifier_same_name_procedure() {
        cache.addProcedureIdentifierHumanReadableName(procedureOther, procedureName);
    }

    @Test
    public void test_other_identifier_name_procedure() {
        cache.addProcedureIdentifierHumanReadableName(procedureOther, procedureNameOther);
    }

    @Test
    public void test_same_identifier_name_obsProp() {
        cache.addObservablePropertyIdentifierHumanReadableName(observedProperty, observedPropertyName);
    }

    @Test
    public void test_same_identifier_other_name_obsProp() {
        cache.addObservablePropertyIdentifierHumanReadableName(observedProperty, observedPropertyNameOther);
    }

    @Test
    public void test_other_identifier_same_name_obsProp() {
        cache.addObservablePropertyIdentifierHumanReadableName(observedPropertyOther, observedPropertyName);
    }

    @Test
    public void test_other_identifier_name_obsProp() {
        cache.addObservablePropertyIdentifierHumanReadableName(observedPropertyOther, observedPropertyNameOther);
    }

    @Test
    public void test_same_identifier_name_offering() {
        cache.addOfferingIdentifierHumanReadableName(offering, offeringName);
    }

    @Test
    public void test_same_identifier_other_name_offering() {
        cache.addOfferingIdentifierHumanReadableName(offering, offeringNameOther);
    }

    @Test
    public void test_other_identifier_same_name_offering() {
        cache.addOfferingIdentifierHumanReadableName(offeringOther, offeringName);
    }

    @Test
    public void test_other_identifier_name_offering() {
        cache.addOfferingIdentifierHumanReadableName(offeringOther, offeringNameOther);
    }

}