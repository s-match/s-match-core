package it.unitn.disi.smatch.loaders;

/**
 * Contains constants for loaders.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public interface ILoader {

    enum LoaderType {FILE, DATABASE, STRING}

    static String XML_FILES = "XML Files (*.xml)";
    static String TXT_FILES = "Text Files (*.txt)";
    static String RDF_FILES = "RDF Files (*.rdf)";
    static String OWL_FILES = "OWL Ontology Files (*.owl)";
    static String SKOS_FILES = "SKOS Files (*.xml)";
}
