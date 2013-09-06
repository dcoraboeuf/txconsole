package net.txconsole.test;


import net.txconsole.core.RunProfile;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:META-INF/spring/*.xml"})
@ActiveProfiles(profiles = {RunProfile.TEST})
public abstract class AbstractIntegrationTest extends AbstractJUnit4SpringContextTests {

}
