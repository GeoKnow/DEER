/**
 *
 */
package org.aksw.deer.learning;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.aksw.deer.vocabulary.SPECS;
import org.aksw.deer.plugin.enrichment.IEnrichmentFunction;
import org.aksw.deer.plugin.operator.IOperator;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.log4j.Logger;

/**
 * @author sherif
 */
public class RefinementNode implements Comparable<RefinementNode> {

  private static final Logger logger = Logger.getLogger(RefinementNode.class.getName());

  public IEnrichmentFunction module = null;
  public IOperator operator = null;
  public double fitness = -Double.MAX_VALUE;
  public List<Model> inputModels = new ArrayList<Model>();
  public List<Model> outputModels = new ArrayList<Model>();
  public Model configModel = ModelFactory.createDefaultModel();
  public List<Resource> inputDatasets = new ArrayList<Resource>();
  public List<Resource> outputDatasets = new ArrayList<Resource>();
  //	public NodeStatus status;


  /**
   * @author sherif
   */
  private RefinementNode(IEnrichmentFunction module, IOperator operator, double fitness,
    List<Model> inputModels,
    List<Model> outputModels, Model configModel, List<Resource> inputDatasets,
    List<Resource> outputDatasets) {
    super();
    if (module != null && operator != null) {
      logger.error(
        "RefinementNodeX can contain either a enrichment or an operator not both. Exit with error!");
      System.exit(1);
    }
    this.module = module;
    this.operator = operator;
    this.fitness = fitness;
    this.inputModels = inputModels;
    this.outputModels = outputModels;
    this.configModel = configModel;
    this.inputDatasets = inputDatasets;
    this.outputDatasets = outputDatasets;
    if (configModel != null) {
      configModel.setNsPrefix(SPECS.prefix, SPECS.uri);
    }
  }

//	/**
//	 * Create a DeerModule refinement node
//	 * @param enrichment
//	 * @param fitness
//	 * @param inputModels
//	 * @param outputModels
//	 * @param configModel
//	 * @param inputDatasets
//	 * @param outputDatasets
//	 *@author sherif
//	 */
//	public RefinementNode(DeerModule enrichment, double fitness, List<Model> inputModels, List<Model> outputModels,
//			Model configModel, List<Resource> inputDatasets, List<Resource> outputDatasets) {
//		this(enrichment, null, fitness, inputModels, outputModels, configModel, inputDatasets, outputDatasets);
//	}


  /**
   * Create a DeerOperator refinement node
   *
   * @author sherif
   */
  public RefinementNode(IOperator operator, double fitness, List<Model> inputModels,
    List<Model> outputModels,
    Model configModel, List<Resource> inputDatasets, List<Resource> outputDatasets) {
    this(null, operator, fitness, inputModels, outputModels, configModel, inputDatasets,
      outputDatasets);
  }


  /**
   * @author sherif
   */
  private RefinementNode(IEnrichmentFunction module, IOperator operator, double fitness, Model inputModel,
    Model outputModel, Model configModel, Resource inputDataset, Resource outputDataset) {
    super();
    if (module != null && operator != null) {
      logger.error(
        "RefinementNodeX can contain either a enrichment or an operator not both. Exit with error!");
      System.exit(1);
    }
    this.module = module;
    this.operator = operator;
    this.fitness = fitness;
    this.inputModels.add(inputModel);
    this.outputModels.add(outputModel);
    this.configModel = configModel;
    this.inputDatasets.add(inputDataset);
    this.outputDatasets.add(outputDataset);
    if (configModel != null) {
      configModel.setNsPrefix(SPECS.prefix, SPECS.uri);
    }
  }


  /**
   * Create a DeerModule refinement node
   *
   * @author sherif
   */
  public RefinementNode(IEnrichmentFunction module, double fitness, Model inputModel,
    Model outputModel, Model configModel, Resource inputDataset, Resource outputDataset) {
    this(module, null, fitness, inputModel, outputModel, configModel, inputDataset, outputDataset);
  }

//	/**
//	 * Create a DeerOperator refinement node
//	 * @param operator
//	 * @param inputModel
//	 * @param outputModel
//	 * @param configModel
//	 * @param inputDataset
//	 * @param outputDataset
//	 *@author sherif
//	 */
//	public RefinementNode(DeerOperator operator, Model inputModel, 
//			Model outputModel, Model configModel, Resource inputDataset, Resource outputDataset) {
//		this(null, operator, -Double.MAX_VALUE, inputModel, outputModel, configModel, inputDataset, outputDataset);
//	}


  /**
   * Create a RefinementNode
   *
   * @author sherif
   */
  public RefinementNode() {
    super();
    configModel.setNsPrefix(SPECS.prefix, SPECS.uri);
  }

  /**
   * Create a RefinementNode with fitness
   *
   * @author sherif
   */
  public RefinementNode(double fitness) {
    this();
    this.fitness = fitness;
  }

  /**
   * @author sherif
   */
  public static void main(String[] args) {

  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    String format = new DecimalFormat("#.###").format(fitness);
    if (module != null) {
      return module.getClass().getSimpleName().replace("Module", "") + "(" + format + ")";
    } else if (operator != null) {
      return operator.getClass().getSimpleName().toUpperCase().replace("OPERATOR", "") + "("
        + format + ")";
    } else {
      return "invalid node";
    }


  }

  /* (non-Javadoc)
   * Compare RefinementNodes based on fitness
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo(RefinementNode o) {
    return (int) (fitness - o.fitness);

  }

  public Model getOutputModel() {
    return outputModels.get(0);
  }

  public Model getInputModel() {
    return inputModels.get(0);
  }

  public Resource getOutputDataset() {
    return outputDatasets.get(0);
  }

  public Resource getInputDataset() {
    return inputDatasets.get(0);
  }
}
