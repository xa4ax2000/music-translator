package org.hyun.music.translator.infrastructure.authenticator;

public class DatabaseStub extends ExternalWebServiceStub {
    @Override
    public String getServiceName(){
        return "Database";
    }
}
