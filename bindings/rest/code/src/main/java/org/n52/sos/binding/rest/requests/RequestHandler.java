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
package org.n52.sos.binding.rest.requests;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;

import net.opengis.sos.x20.CapabilitiesDocument;
import net.opengis.sos.x20.CapabilitiesType;
import net.opengis.sos.x20.ObservationOfferingDocument;
import net.opengis.sos.x20.ObservationOfferingType;
import net.opengis.swes.x20.AbstractContentsType.Offering;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.iceland.coding.CodingRepository;
import org.n52.iceland.coding.encode.EncoderResponseUnsupportedException;
import org.n52.iceland.exception.ows.concrete.ServiceOperatorNotFoundException;
import org.n52.iceland.service.operator.ServiceOperator;
import org.n52.iceland.service.operator.ServiceOperatorRepository;
import org.n52.janmayen.Comparables;
import org.n52.janmayen.http.MediaTypes;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.ows.service.GetCapabilitiesRequest;
import org.n52.shetland.ogc.ows.service.OwsServiceKey;
import org.n52.shetland.ogc.ows.service.OwsServiceRequest;
import org.n52.shetland.ogc.ows.service.OwsServiceResponse;
import org.n52.sos.binding.rest.Constants;
import org.n52.svalbard.OperationKey;
import org.n52.svalbard.encode.Encoder;
import org.n52.svalbard.encode.OperationResponseEncoderKey;
import org.n52.svalbard.encode.exception.EncodingException;
import org.n52.svalbard.encode.exception.NoEncoderForKeyException;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 */
public abstract class RequestHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestHandler.class);

    protected Constants bindingConstants = Constants.getInstance();

    public abstract RestResponse handleRequest(RestRequest request) throws OwsExceptionReport, XmlException, IOException;

    protected OwsExceptionReport logRequestTypeNotSupportedByThisHandlerAndCreateException(final RestRequest request, final String requestHandlerClassName) throws OwsExceptionReport
    {
        final String exceptionText = String.format("Handling of request failed but no exception is thrown. The request '%s' is not supported by this request handler '%s'",
                request!=null?request.getClass().getName():"DeleteObservationRequest parameter is null!",
                requestHandlerClassName);
        LOGGER.debug(exceptionText);
        return new EncoderResponseUnsupportedException().withMessage(exceptionText);
    }

    protected ObservationOfferingType getOfferingForProcedureFromSos(final GetCapabilitiesRequest getCapabilitiesRequest, final String procedureId) throws OwsExceptionReport, XmlException, IOException
    {
        final Offering[] xb_offerings = getOfferingsFromSosCore(getCapabilitiesRequest);
        if (xb_offerings != null) {
            for (final Offering xb_offering : xb_offerings) {
                final ObservationOfferingType xb_observationOffering = ObservationOfferingDocument.Factory.parse(xb_offering.newInputStream()).getObservationOffering();
                if (xb_observationOffering.getProcedure() != null &&
                        xb_observationOffering.getProcedure().equals(procedureId) ) {
                    return xb_observationOffering;
                }
            }
        }
        return null;
    }

    protected Offering[] getOfferingsFromSosCore(GetCapabilitiesRequest req) throws OwsExceptionReport, XmlException
    {
        // if response is an OWSException report -> cancel whole process and throw it
        try {
        XmlObject xb_getCapabilitiesResponse = executeSosRequest(req);
        if (xb_getCapabilitiesResponse instanceof CapabilitiesDocument) {
            final CapabilitiesDocument xb_capabilitiesDocument = (CapabilitiesDocument) xb_getCapabilitiesResponse;
            final CapabilitiesType xb_capabilities = xb_capabilitiesDocument.getCapabilities();

            if (isOfferingArrayAvailable(xb_capabilities))
            {
                return xb_capabilities.getContents().getContents().getOfferingArray();
            }
            return new Offering[0];
        }
        else
        {
            final String exceptionText = String.format("Processing of SOS core operation \"GetCapabilities\" response failed. Type of could not be handled: \"%s\"",
                    xb_getCapabilitiesResponse.getClass().getName());
            LOGGER.debug(exceptionText);
            throw new NoApplicableCodeException().withMessage(exceptionText);
        }
        } catch (EncodingException ee) {
            throw new NoApplicableCodeException().causedBy(ee);
        }
    }

    private boolean isOfferingArrayAvailable(final CapabilitiesType xb_capabilities)
    {
        return xb_capabilities.getContents() != null &&
                xb_capabilities.getContents().getContents() != null &&
                xb_capabilities.getContents().getContents().getOfferingArray() != null;
    }

    protected ObservationOfferingType getObservationOfferingFromOffering(final Offering xb_offering) throws XmlException, IOException
    {
        return ObservationOfferingDocument.Factory.parse(xb_offering.newInputStream()).getObservationOffering();
    }

    private ServiceOperator getServiceOperator(OwsServiceRequest req) throws OwsExceptionReport
    {
        String service = req.getService();
        String version = req.getVersion();
        ServiceOperatorRepository serviceOperatorRepository = ServiceOperatorRepository.getInstance();
        if (req instanceof GetCapabilitiesRequest) {
            GetCapabilitiesRequest gcr = (GetCapabilitiesRequest) req;
            if (gcr.isSetAcceptVersions()) {
                return gcr.getAcceptVersions().stream().map(v -> new OwsServiceKey(service, v))
                        .map(serviceOperatorRepository::getServiceOperator).filter(Objects::nonNull).findFirst()
                        .orElseThrow(() -> new ServiceOperatorNotFoundException(req));
            } else {
                Set<String> supportedVersions = serviceOperatorRepository
                        .getSupportedVersions(service);
                String newest = supportedVersions.stream().max(Comparables.version())
                        .orElseThrow(() -> new ServiceOperatorNotFoundException(req));
                return serviceOperatorRepository.getServiceOperator(new OwsServiceKey(service, newest));
            }
        } else {
            return serviceOperatorRepository.getServiceOperator(new OwsServiceKey(service, version));
        }
    }

    protected XmlObject executeSosRequest(OwsServiceRequest request) throws EncodingException, OwsExceptionReport {
        return encodeResponse(getServiceOperator(request).receiveRequest(request));
    }

    private XmlObject encodeResponse(OwsServiceResponse response)
            throws EncodingException {
        OperationResponseEncoderKey key = new OperationResponseEncoderKey(
                new OperationKey(response), MediaTypes.TEXT_XML);
        Encoder<XmlObject, OwsServiceResponse> encoder =
                CodingRepository.getInstance().getEncoder(key);
        if (encoder == null) {
            throw new NoEncoderForKeyException(key);
        }
        return encoder.encode(response);
    }

}
