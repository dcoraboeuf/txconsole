package net.txconsole.backend;

import net.txconsole.backend.security.AbstractSecurityTest;
import net.txconsole.backend.support.SimpleTranslationSource;
import net.txconsole.backend.support.SimpleTranslationSourceConfig;
import net.txconsole.core.model.*;
import net.txconsole.extension.format.properties.PropertiesTxFileFormat;
import net.txconsole.extension.format.properties.PropertiesTxFileFormatConfig;
import net.txconsole.extension.format.properties.PropertyGroup;
import net.txconsole.extension.svn.SVNTxFileSource;
import net.txconsole.extension.svn.SVNTxFileSourceConfig;
import net.txconsole.service.StructureService;
import net.txconsole.service.support.Configured;
import net.txconsole.service.support.TranslationSource;
import net.txconsole.service.support.TxFileFormat;
import net.txconsole.service.support.TxFileSource;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.Callable;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class StructureServiceIntegrationTest extends AbstractSecurityTest {

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private StructureService structureService;
    @Autowired
    private SimpleTranslationSource simpleTranslationSource;
    @Autowired
    private SVNTxFileSource svnTxFileSource;
    @Autowired
    private PropertiesTxFileFormat propertiesTxFileFormat;

    @Test
    public void saveAndGetConfigurationForBranch() throws Exception {
        // Prerequisites
        // Create a project
        final ProjectSummary project = asAdmin().call(new Callable<ProjectSummary>() {
            @Override
            public ProjectSummary call() throws Exception {
                return structureService.createProject(new ProjectCreationForm(
                        "SSIP1",
                        "saveAndGetConfigurationForBranch",
                        Arrays.asList("en", "fr"),
                        new JsonConfiguration(
                                "simple",
                                simpleTranslationSource.writeConfiguration(
                                        new SimpleTranslationSourceConfig<>(
                                                new Configured<SVNTxFileSourceConfig, TxFileSource<SVNTxFileSourceConfig>>(
                                                        new SVNTxFileSourceConfig(
                                                                "http://test/project/$BRANCH_PATH/translations",
                                                                "translator",
                                                                "xxx"
                                                        ),
                                                        svnTxFileSource
                                                ),
                                                new Configured<PropertiesTxFileFormatConfig, TxFileFormat<PropertiesTxFileFormatConfig>>(
                                                        new PropertiesTxFileFormatConfig(
                                                                asList(
                                                                        new PropertyGroup("common", asList(Locale.ENGLISH, Locale.FRENCH)),
                                                                        new PropertyGroup("lux", asList(Locale.ENGLISH, Locale.FRENCH, Locale.GERMAN))
                                                                )
                                                        ),
                                                        propertiesTxFileFormat
                                                )
                                        )
                                )
                        )
                ));
            }
        });
        // Creates a branch for this project
        BranchSummary branch = asAdmin().call(new Callable<BranchSummary>() {
            @Override
            public BranchSummary call() throws Exception {
                return structureService.createBranch(project.getId(), new BranchCreationForm(
                        "B1",
                        Arrays.asList(new ParameterValueForm("BRANCH_PATH", "branches/B1"))
                ));
            }
        });

        // Gets the configuration for this branch
        Configured<Object, TranslationSource<Object>> translationSource = structureService.getConfiguredTranslationSource(branch.getId());

        // Checks
        assertNotNull(translationSource);
        assertTrue(translationSource.getConfigurable() instanceof SimpleTranslationSource);
        assertEquals(
                new SimpleTranslationSourceConfig<>(
                        new Configured<SVNTxFileSourceConfig, TxFileSource<SVNTxFileSourceConfig>>(
                                new SVNTxFileSourceConfig(
                                        "http://test/project/branches/B1/translations", // Replacement here !
                                        "translator",
                                        "xxx"
                                ),
                                svnTxFileSource
                        ),
                        new Configured<PropertiesTxFileFormatConfig, TxFileFormat<PropertiesTxFileFormatConfig>>(
                                new PropertiesTxFileFormatConfig(
                                        asList(
                                                new PropertyGroup("common", asList(Locale.ENGLISH, Locale.FRENCH)),
                                                new PropertyGroup("lux", asList(Locale.ENGLISH, Locale.FRENCH, Locale.GERMAN))
                                        )
                                ),
                                propertiesTxFileFormat
                        )
                ),
                translationSource.getConfiguration());
    }

}
