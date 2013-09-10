package net.txconsole.web.resource;

import com.netbeetle.jackson.ObjectMapperFactory;
import net.txconsole.core.model.BranchSummary;
import net.txconsole.core.security.ProjectFunction;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.springframework.hateoas.Link;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class ResourceTest {

    private final ObjectMapper mapper = ObjectMapperFactory.createObjectMapper();

    @Test
    public void json() throws IOException {
        BranchSummary o = new BranchSummary(10, 1, "B1");
        Resource<BranchSummary> r = new Resource<>(o)
                .withLink(new Link("http://host/ui/branch/10").withSelfRel())
                .withLink(new Link("http://host/branch/10").withRel(Resource.REL_GUI))
                        // Project link
                .withLink(new Link("http://host/ui/project/1").withRel("project"))
                        // ACL
                .withAction(ProjectFunction.UPDATE, false)
                .withAction(ProjectFunction.DELETE, false)
                .withAction(ProjectFunction.REQUEST_CREATE, true);
        String json = mapper.writeValueAsString(r);
        assertEquals("{\"links\":[{\"rel\":\"self\",\"href\":\"http://host/ui/branch/10\"},{\"rel\":\"gui\",\"href\":\"http://host/branch/10\"},{\"rel\":\"project\",\"href\":\"http://host/ui/project/1\"}],\"data\":{\"id\":10,\"projectId\":1,\"name\":\"B1\"},\"actions\":[\"PROJECT#REQUEST_CREATE\"]}", json);
    }

}
