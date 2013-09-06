package net.txconsole.service.security;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ProjectGrant {

    public ProjectFunction value();

}
