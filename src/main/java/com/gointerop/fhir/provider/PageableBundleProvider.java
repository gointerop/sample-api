package com.gointerop.fhir.provider;

import ca.uhn.fhir.rest.api.server.IBundleProvider;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IPrimitiveType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;

public class PageableBundleProvider implements IBundleProvider {
    private final List<? extends IBaseResource> page;

    public PageableBundleProvider(List<? extends IBaseResource> content) {
        page = content;
    }

    @Override
    public IPrimitiveType<Date> getPublished() {
        return null;
    }

    @Nonnull
    @Override
    public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
        return (List<IBaseResource>) page.subList(theFromIndex, theToIndex);
    }

    @Nullable
    @Override
    public String getUuid() {
        return null;
    }

    @Override
    public Integer preferredPageSize() {
        return page.size();
    }

    @Nullable
    @Override
    public Integer size() {
        return (int) page.size();
    }
}
