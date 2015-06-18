package com.github.p4535992.util.jenaAndSesame;

import org.openrdf.model.impl.TreeModel;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.impl.StatementImpl;

import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.BNode;
import org.openrdf.model.Graph;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
/**
 * Created by 4535992 on 10/06/2015.
 */
public class JenaAndSesame {

    /**
     * Method to convert a Sesame Dataset to a JenaModel
     * @param repository
     * @return
     * @throws org.openrdf.repository.RepositoryException
     */
  /*  public static com.hp.hpl.jena.rdf.model.Model convertSesameDataSetToJenaModel(
            org.openrdf.repository.Repository repository )
            throws org.openrdf.repository.RepositoryException{
        org.openrdf.repository.RepositoryConnection repositoryConnection = repository.getConnection();
        // finally, insert the DatasetGraph instance
        com.ontotext.jena.SesameDataset dataset = new SesameDataset(repositoryConnection);
        //From now on the SesameDataset object can be used through the Jena API
        //as regular Dataset, e.home. to add some data into it one could something like the
        //following:
        com.hp.hpl.jena.rdf.model.Model model =
                com.hp.hpl.jena.rdf.model.ModelFactory.createModelForGraph(dataset.getDefaultGraph());
        return model;
    }*/


    /**
     * Internal model used to create instances of Jena API objects
     */
    private static final Model mInternalModel = ModelFactory.createDefaultModel();

    /**
     * Sesame value factory for creating instances of Sesame API objects
     */
    private static final ValueFactory FACTORY = new ValueFactoryImpl();

    /**
     * Convert the given Jena Resource into a Sesame Resource
     * @param theRes the jena resource to convert
     * @return the jena resource as a sesame resource
     */
    public static org.openrdf.model.Resource asSesameResource(Resource theRes) {
        if (theRes == null) {
            return null;
        }
        else if (theRes.canAs(Property.class)) {
            return asSesameURI(theRes.as(Property.class));
        }
        else {
            return FACTORY.createBNode(theRes.getId().getLabelString());
        }
    }

    /**
     * Convert the given Jena Property instance to a Sesame URI instance
     * @param theProperty the Jena Property to convert
     * @return the Jena property as a Sesame Instance
     */
    public static org.openrdf.model.URI asSesameURI(Property theProperty) {
        if (theProperty == null) {
            return null;
        }
        else {
            return FACTORY.createURI(theProperty.getURI());
        }
    }

    /**
     * Convert the given Jena Literal to a Sesame Literal
     * @param theLiteral the Jena Literal to convert
     * @return the Jena Literal as a Sesame Literal
     */
    public static org.openrdf.model.Literal asSesameLiteral(Literal theLiteral) {
        if (theLiteral == null) {
            return null;
        }
        else if (theLiteral.getLanguage() != null && !theLiteral.getLanguage().equals("")) {
            return FACTORY.createLiteral(theLiteral.getLexicalForm(),
                    theLiteral.getLanguage());
        }
        else if (theLiteral.getDatatypeURI() != null) {
            return FACTORY.createLiteral(theLiteral.getLexicalForm(),
                    FACTORY.createURI(theLiteral.getDatatypeURI()));
        }
        else {
            return FACTORY.createLiteral(theLiteral.getLexicalForm());
        }
    }

    /**
     * Convert the given Jena node as a Sesame Value
     * @param theNode the Jena node to convert
     * @return the jena node as a Sesame Value
     */
    public static Value asSesameValue(RDFNode theNode) {
        if (theNode == null) {
            return null;
        }
        else if (theNode.canAs(Literal.class)) {
            return asSesameLiteral(theNode.as(Literal.class));
        }
        else {
            return asSesameResource(theNode.as(Resource.class));
        }
    }

    /**
     * Convert the given Sesame Resource to a Jena Resource
     * @param theRes the sesame resource to convert
     * @return the sesame resource as a jena resource
     */
    public static com.hp.hpl.jena.rdf.model.Resource asJenaResource(org.openrdf.model.Resource theRes) {
        if (theRes == null) {
            return null;
        }
        else if (theRes instanceof URI) {
            return asJenaURI( (URI) theRes);
        }
        else {
            return mInternalModel.createResource(new AnonId(((BNode) theRes).getID()));
        }
    }

    /**
     * Convert the sesame value to a Jena Node
     * @param theValue the Sesame value
     * @return the sesame value as a Jena node
     */
    public static RDFNode asJenaNode(Value theValue) {
        if (theValue instanceof org.openrdf.model.Literal) {
            return asJenaLiteral( (org.openrdf.model.Literal) theValue);
        }
        else {
            return asJenaResource( (org.openrdf.model.Resource) theValue);
        }
    }

    /**
     * Convert the Sesame URI to a Jena Property
     * @param theURI the sesame URI
     * @return the URI as a Jena property
     */
    public static Property asJenaURI(URI theURI) {
        if (theURI == null) {
            return null;
        }
        else {
            return mInternalModel.getProperty(theURI.toString());
        }
    }

    /**
     * Convert a Sesame Literal to a Jena Literal
     * @param theLiteral the Sesame literal
     * @return the sesame literal converted to Jena
     */
    public static com.hp.hpl.jena.rdf.model.Literal asJenaLiteral(org.openrdf.model.Literal theLiteral) {
        if (theLiteral == null) {
            return null;
        }
        else if (theLiteral.getLanguage() != null) {
            return mInternalModel.createLiteral(theLiteral.getLabel(),
                    theLiteral.getLanguage());
        }
        else if (theLiteral.getDatatype() != null) {
            return mInternalModel.createTypedLiteral(theLiteral.getLabel(),
                    theLiteral.getDatatype().toString());
        }
        else {
            return mInternalModel.createLiteral(theLiteral.getLabel());
        }
    }

    /**
     * Convert the Sesame Graph to a Jena Model
     * @param theGraph the Graph to convert
     * @return the set of statements in the Sesame Graph converted and saved in a Jena Model
     */
    public static Model asJenaModel(Graph theGraph) {
        Model aModel = ModelFactory.createDefaultModel();

        for (final org.openrdf.model.Statement aStmt : theGraph) {
            aModel.add(asJenaStatement(aStmt));
        }

        return aModel;
    }

    /**
     * Convert the Jena Model to a Sesame Model
     * @param theModel the model to convert
     * @return the set of statements in the Jena model saved in a sesame Graph
     */
    public static org.openrdf.model.Model asSesameModel(Model theModel) {
        org.openrdf.model.Model sesameModel = new TreeModel();
        StmtIterator sIter = theModel.listStatements();
        while (sIter.hasNext()) {
            sesameModel.add(asSesameStatement(sIter.nextStatement()));
        }
        sIter.close();
        return sesameModel;
    }

    /**
     * Convert a Jena Statement to a Sesame statement
     * @param theStatement the statement to convert
     * @return the equivalent Sesame statement
     */
    public static org.openrdf.model.Statement asSesameStatement(Statement theStatement) {
        return new StatementImpl(asSesameResource(theStatement.getSubject()),
                asSesameURI(theStatement.getPredicate()),
                asSesameValue(theStatement.getObject()));
    }

    /**
     * Convert a Sesame statement to a Jena statement
     * @param theStatement the statemnet to convert
     * @return the equivalent Jena statement
     */
    public static Statement asJenaStatement(org.openrdf.model.Statement theStatement) {
        return mInternalModel.createStatement(asJenaResource(theStatement.getSubject()),
                asJenaURI(theStatement.getPredicate()),
                asJenaNode(theStatement.getObject()));
    }

//	/**
//	 * An implementation of the Sesame ValueFactory interface which relaxes Sesame's opressive constraint that
//	 * the URI *must* be a valid URI, ie something with a namespace & a local name.
//	 */
//	private static class LaxSesameValueFactory extends ValueFactoryImpl {
//
//		/**
//		 * Creates a new Sesame URI object from the URI string, which can be just a local name, in which case the
//		 * namespace of the URI object will be the empty string.
//		 * @inheritDoc
//		 */
//		@Override
//		public URI createURI(String theURI) {
//			try {
//				return super.createURI(theURI);
//			}
//			catch (IllegalArgumentException e) {
//				return new URIImpl("", theURI);
//			}
//		}
//	}







}
