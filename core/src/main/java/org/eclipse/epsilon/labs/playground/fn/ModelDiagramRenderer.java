package org.eclipse.epsilon.labs.playground.fn;

import io.micronaut.cache.annotation.Cacheable;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import org.eclipse.epsilon.egl.EglModule;
import org.eclipse.epsilon.eol.execute.context.Variable;
import org.eclipse.epsilon.eol.models.Model;
import org.eclipse.epsilon.labs.playground.execution.ScriptTimeoutTerminator;
import org.eclipse.epsilon.labs.playground.fn.emfatic2plantuml.MetamodelDiagramResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Singleton
public class ModelDiagramRenderer {

  @Inject
  ModelLoader modelLoader;

  @Inject
  ScriptTimeoutTerminator timeoutTerminator;

  public MetamodelDiagramResponse generateMetamodelDiagram(String emfatic) throws Exception {
    MetamodelDiagramResponse response = new MetamodelDiagramResponse();
    response.setMetamodelDiagram(renderPlantUML(emfatic2plantuml(emfatic)));
    return response;
  }

  public ModelDiagramResponse generateModelDiagram(Model model, Variable... variables) throws Exception {
    ModelDiagramResponse diag = new ModelDiagramResponse();
    diag.setModelDiagram(renderPlantUML(model2plantuml(model, variables)));
    return diag;
  }

  @Cacheable("flexmi-to-svg")
  public ModelDiagramResponse generateDiagramFromFlexmi(String flexmi, String emfatic) throws Exception {
    Model model = modelLoader.getInMemoryFlexmiModel(flexmi, emfatic);
    ModelDiagramResponse diag = new ModelDiagramResponse();
    diag.setModelDiagram(renderPlantUML(model2plantuml(model)));
    return diag;
  }

  @Cacheable("xmi-to-svg")
  public ModelDiagramResponse generateDiagramFromXmi(String xmi, String emfatic) throws Exception {
    Model model = modelLoader.getInMemoryXmiModel(xmi, emfatic);
    ModelDiagramResponse diag = new ModelDiagramResponse();
    diag.setModelDiagram(renderPlantUML(model2plantuml(model)));
    return diag;
  }

  protected String model2plantuml(Model model, Variable... variables) throws Exception {
    EglModule module = new EglModule();
    module.parse(getClass().getResource("/flexmi2plantuml.egl").toURI());
    model.setName("M");
    module.getContext().getModelRepository().addModel(model);
    module.getContext().getFrameStack().put(variables);
    timeoutTerminator.scheduleScriptTimeout(module);

    try {
      return module.execute() + "";
    } finally {
      module.getContext().getModelRepository().dispose();
      module.getContext().dispose();
    }
  }

  @Cacheable("emfatic-to-plantuml")
  protected String emfatic2plantuml(String emfatic) throws Exception {
    Model model = modelLoader.getInMemoryEmfaticModel(emfatic);
    model.setName("M");

    EglModule module = new EglModule();
    module.parse(getClass().getResource("/emfatic2plantuml.egl").toURI());
    module.getContext().getModelRepository().addModel(model);
    timeoutTerminator.scheduleScriptTimeout(module);

    try {
      return module.execute() + "";
    } finally {
      module.getContext().getModelRepository().dispose();
      module.getContext().dispose();
    }
  }

  @Cacheable("plantuml-to-svg")
  protected String renderPlantUML(String plantUml) throws IOException {
    SourceStringReader reader = new SourceStringReader(plantUml);
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    reader.outputImage(os, new FileFormatOption(FileFormat.SVG));
    os.close();

    return os.toString(StandardCharsets.UTF_8);
  }

}
