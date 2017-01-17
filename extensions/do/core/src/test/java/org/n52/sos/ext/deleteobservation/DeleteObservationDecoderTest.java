/*
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ext.deleteobservation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.n52.shetland.ogc.sos.SosConstants.SOS;
import static org.n52.shetland.ogc.sos.delobs.DeleteObservationConstants.CONFORMANCE_CLASSES;
import static org.n52.shetland.ogc.sos.delobs.DeleteObservationConstants.NS_SOSDO_1_0;
import static org.n52.shetland.util.CollectionHelper.union;
import static org.n52.svalbard.util.CodingHelper.decoderKeysForElements;
import static org.n52.svalbard.util.CodingHelper.xmlDecoderKeysForOperation;

import java.util.Set;

import net.opengis.sosdo.x10.DeleteObservationDocument;

import org.apache.xmlbeans.XmlObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.svalbard.decode.DecoderKey;
import org.n52.svalbard.decode.DeleteObservationDecoder;
import org.n52.svalbard.decode.exception.DecodingException;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.delobs.DeleteObservationConstants;
import org.n52.shetland.ogc.sos.request.DeleteObservationRequest;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * @since 4.0.0
 */
public class DeleteObservationDecoderTest {

    private static DeleteObservationDecoder instance;

    private static Set<DecoderKey> dkt;

    private static XmlObject incorrectXmlObject;

    /*
     * <?xml version="1.0" encoding="UTF-8"?> <sosdo:DeleteObservation
     * version="2.0" service="SOS"
     * xmlns:sosdo="http://www.opengis.net/sosdo/1.0">
     * <sosdo:observation>test-observation-identifier</sosdo:observation>
     * </sosdo:DeleteObservation>
     */
    private static DeleteObservationDocument correctXmlObject;

    private static String observationId = "test_obs_id";


    @Before
    public void initInstance() {
        instance = new DeleteObservationDecoder();
        correctXmlObject = DeleteObservationDocument.Factory.newInstance();
        correctXmlObject.addNewDeleteObservation().setObservation(observationId);
    }

    @Test
    public void constructorReturnsInstance() {
        String className = DeleteObservationDecoder.class.getName();
        assertNotNull("Instance is null. Constructor failed", instance);
        assertTrue("Instance of constructed object is not of class" + className,
                instance.getClass().getName().equals(className));
    }

    @Test
    public void testGetDecoderKeyTypes() {
        assertNotNull("DecoderKey is null", instance.getKeys());
        assertTrue("DecoderKey does NOT equal " + dkt, instance.getKeys().equals(dkt));
    }

    @Test
    public void testGetConformanceClasses() {
        assertNotNull("ConformanceClasses is null", instance.getConformanceClasses(SosConstants.SOS, Sos2Constants.SERVICEVERSION));
        assertTrue("ConformanceClasses contains " + CONFORMANCE_CLASSES,
                instance.getConformanceClasses(SosConstants.SOS, Sos2Constants.SERVICEVERSION).equals(CONFORMANCE_CLASSES));
    }

    @Test
    public void getSupportedTypesReturnsEmptyList() {
        assertNotNull("Supported Types is null", instance.getSupportedTypes());
        assertEquals("Supported Types size ", 0, instance.getSupportedTypes().size());
    }

    @Test(expected = OwsExceptionReport.class)
    public void decodeNullThrowsOwsExceptionReport() throws DecodingException {
        instance.decode(null);
    }

    @Test(expected = OwsExceptionReport.class)
    public void decodingIncorrectXmlObjectThrowsOwsExceptionReport() throws DecodingException {
        instance.decode(incorrectXmlObject);
    }

    @Test
    public void decodingCorrectXmlObjectReturnsCorrectServiceRequest() throws DecodingException {
        String className = DeleteObservationRequest.class.getName();
        assertNotNull("Decoding of correct XmlObject returned null", instance.decode(correctXmlObject));
        assertEquals("Class of Result ", className, instance.decode(correctXmlObject).getClass().getName());
        assertEquals("Id of observation to delete", observationId, instance.decode(correctXmlObject)
                .getObservationIdentifier());
    }

    @Test(expected = OwsExceptionReport.class)
    public void should_throw_OwsExceptionReport_when_receving_invalid_DeleteObservationDocument()
            throws DecodingException {
        correctXmlObject = DeleteObservationDocument.Factory.newInstance();
        instance.decode(correctXmlObject);
    }

    @SuppressWarnings("unchecked")
    @BeforeClass
    public static void initFixtures() {
        incorrectXmlObject = XmlObject.Factory.newInstance();
        dkt =
                union(decoderKeysForElements(NS_SOSDO_1_0, DeleteObservationDocument.class),
                      xmlDecoderKeysForOperation(SOS, Sos2Constants.SERVICEVERSION,
                                                        DeleteObservationConstants.Operations.DeleteObservation));
    }

}
