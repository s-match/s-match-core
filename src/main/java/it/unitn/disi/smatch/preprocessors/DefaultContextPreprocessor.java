package it.unitn.disi.smatch.preprocessors;

import it.unitn.disi.smatch.SMatchConstants;
import it.unitn.disi.smatch.data.ling.IAtomicConceptOfLabel;
import it.unitn.disi.smatch.data.ling.ISense;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.oracles.ILinguisticOracle;
import it.unitn.disi.smatch.oracles.ISenseMatcher;
import it.unitn.disi.smatch.oracles.LinguisticOracleException;
import it.unitn.disi.smatch.oracles.SenseMatcherException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Performs all the operations related to linguistic preprocessing.
 * It also contains some heuristics to perform sense disambiguation.
 * Corresponds to Step 1 and 2 in the semantic matching algorithm.
 *
 * @author Mikalai Yatskevich mikalai.yatskevich@comlab.ox.ac.uk
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 * @author Moaz Reyad <reyad@disi.unitn.it>
 */
public class DefaultContextPreprocessor implements IContextPreprocessor {

    private static final Logger log = LoggerFactory.getLogger(DefaultContextPreprocessor.class);

    /**
     * the words which are cut off from the area of discourse
     */
    public static final String DEFAULT_MEANINGLESS_WORDS = "of on to their than from for by in at is are have has the a as with your etc our into its his her which him among those against ";
    /**
     * the words which are treated as logical and (&)
     */
    public static final String DEFAULT_AND_WORDS = " + & ^ ";
    /**
     * the words which are treated as logical or (|)
     */
    public static final String DEFAULT_OR_WORDS = " and or | , ";
    /**
     * the words which are treated as logical not (~)
     */
    public static final String DEFAULT_NOT_WORDS = " except non without ";
    /**
     * number characters
     */
    public static final String DEFAULT_NUMBER_CHARACTERS = "1234567890";

    private final ISenseMatcher senseMatcher;
    private final ILinguisticOracle linguisticOracle;

    private final boolean debugLabels;
    private final boolean debugUnrecognizedWords;

    private final String meaninglessWords;
    private final String andWords;
    private final String orWords;
    private final String notWords;
    private final String numberCharacters;

    public DefaultContextPreprocessor(ISenseMatcher senseMatcher, ILinguisticOracle linguisticOracle) {
        this.senseMatcher = senseMatcher;
        this.linguisticOracle = linguisticOracle;

        this.debugLabels = false;
        this.debugUnrecognizedWords = false;

        this.meaninglessWords = DEFAULT_MEANINGLESS_WORDS;
        this.andWords = DEFAULT_AND_WORDS;
        this.orWords = DEFAULT_OR_WORDS;
        this.notWords = DEFAULT_NOT_WORDS;
        this.numberCharacters = DEFAULT_NUMBER_CHARACTERS;
    }

    public DefaultContextPreprocessor(ISenseMatcher senseMatcher, ILinguisticOracle linguisticOracle,
                                      boolean debugLabels, boolean debugUnrecognizedWords) {
        this.senseMatcher = senseMatcher;
        this.linguisticOracle = linguisticOracle;

        this.debugLabels = debugLabels;
        this.debugUnrecognizedWords = debugUnrecognizedWords;

        this.meaninglessWords = DEFAULT_MEANINGLESS_WORDS;
        this.andWords = DEFAULT_AND_WORDS;
        this.orWords = DEFAULT_OR_WORDS;
        this.notWords = DEFAULT_NOT_WORDS;
        this.numberCharacters = DEFAULT_NUMBER_CHARACTERS;
    }

    public DefaultContextPreprocessor(ISenseMatcher senseMatcher, ILinguisticOracle linguisticOracle,
                                      boolean debugLabels, boolean debugUnrecognizedWords,
                                      String meaninglessWords, String andWords,
                                      String orWords, String notWords, String numberCharacters) {
        this.senseMatcher = senseMatcher;
        this.linguisticOracle = linguisticOracle;

        this.debugLabels = debugLabels;
        this.debugUnrecognizedWords = debugUnrecognizedWords;

        this.meaninglessWords = meaninglessWords;
        this.andWords = andWords;
        this.orWords = orWords;
        this.notWords = notWords;
        this.numberCharacters = numberCharacters;
    }

    /**
     * Performs all preprocessing procedures as follows:
     * - linguistic analysis (each lemma is associated with the set of senses taken from the oracle).
     * - sense filtering (elimination of irrelevant to context structure senses)
     *
     * @param context context to be preprocessed
     * @throws ContextPreprocessorException ContextPreprocessorException
     */
    public void preprocess(IContext context) throws ContextPreprocessorException {
        Set<String> unrecognizedWords = new HashSet<>();

        // construct cLabs
        context = buildCLabs(context, unrecognizedWords);
        // sense filtering
        context = findMultiwordsInContextStructure(context);
        try {
            senseFiltering(context);
        } catch (SenseMatcherException e) {
            throw new ContextPreprocessorException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }

        log.debug("Unrecognized words: " + unrecognizedWords.size());
        if (debugUnrecognizedWords) {
            Set<String> sortedWords = new TreeSet<>(unrecognizedWords);
            for (String unrecognizedWord : sortedWords) {
                log.debug("Unrecognized word: " + unrecognizedWord);
            }
        }
        unrecognizedWords.clear();
    }

    /**
     * Constructs cLabs for all nodes of the context.
     *
     * @param context           context of node which cLab to be build
     * @param unrecognizedWords unrecognized words
     * @return context with cLabs
     * @throws ContextPreprocessorException ContextPreprocessorException
     */
    private IContext buildCLabs(IContext context, Set<String> unrecognizedWords) throws ContextPreprocessorException {
        int counter = 0;
        int total = context.getRoot().getDescendantCount() + 1;
        int reportInt = (total / 20) + 1;//i.e. report every 5%

        for (Iterator<INode> i = context.getNodes(); i.hasNext(); ) {
            processNode(i.next(), unrecognizedWords);

            counter++;
            if ((SMatchConstants.LARGE_TREE < total) && (0 == (counter % reportInt)) && log.isInfoEnabled()) {
                log.info(100 * counter / total + "%");
            }
        }

        return context;
    }

    /**
     * Creates concept of a label formula.
     *
     * @param node              node to process
     * @param unrecognizedWords unrecognized words
     * @throws ContextPreprocessorException ContextPreprocessorException
     */
    public void processNode(INode node, Set<String> unrecognizedWords) throws ContextPreprocessorException {
        try {
            // reset old preprocessing
            node.getNodeData().setcLabFormula("");
            node.getNodeData().setcNodeFormula("");
            while (0 < node.getNodeData().getACoLCount()) {
                node.getNodeData().removeACoL(0);
            }

            boolean isEmpty = true;
            String labelOfNode = node.getNodeData().getName().trim();

            if (debugLabels) {
                log.debug("preprocessing: " + labelOfNode);
            }

            labelOfNode = replacePunctuation(labelOfNode);
            labelOfNode = labelOfNode.toLowerCase();
            List<ISense> wnSense = new ArrayList<>();
            if (!(("top".equals(labelOfNode) || "thing".equals(labelOfNode)) && !node.hasParent()) && (!meaninglessWords.contains(labelOfNode + " ")) && (isTokenMeaningful(labelOfNode))) {
                wnSense = linguisticOracle.getSenses(labelOfNode);
            }

            // identifiers of meaningful tokens in
            String meaningfulTokens = " ";
            // tokens of the label of node
            List<String> tokensOfNodeLabel = new ArrayList<>();

            // is the label a WordNet entry?
            int id_tok = 0;
            if (0 < wnSense.size()) {
                // add to list of processed labels
                tokensOfNodeLabel.add(labelOfNode);
                List<String> lemmas = linguisticOracle.getBaseForms(labelOfNode);
                String lemma = labelOfNode;
                if (0 < lemmas.size()) {
                    lemma = lemmas.get(0);
                }

                // create atomic node of label
                IAtomicConceptOfLabel ACoL = createACoL(node, id_tok, labelOfNode, lemma);
                // to token ids
                meaningfulTokens = meaningfulTokens + id_tok + " ";
                // add senses to ACoL
                for (ISense sense : wnSense) {
                    ACoL.addSense(sense);
                }
                isEmpty = false;
                id_tok++;
            } else {
                // The label of node is not in WN
                // Split the label by words
                StringTokenizer lemmaTokenizer = new StringTokenizer(labelOfNode, " _()[]/'\\#1234567890");
                ArrayList<String> tokens = new ArrayList<>();
                while (lemmaTokenizer.hasMoreElements()) {
                    tokens.add(lemmaTokenizer.nextToken());
                }

                // perform multiword recognition
                tokens = multiwordRecognition(tokens);
                // for all tokens in label
                for (int i = 0; i < tokens.size(); i++) {
                    String token = tokens.get(i).trim();
                    // if the token is not meaningless
                    if ((!meaninglessWords.contains(token + " ")) && (isTokenMeaningful(token))) {
                        // add to list of processed tokens
                        tokensOfNodeLabel.add(token);
                        // if not logical connective
                        if ((!andWords.contains(token)) && ((orWords.indexOf(token)) == -1)
                                && ((notWords.indexOf(token)) == -1) && (!isNumber(token))) {
                            // get WN senses for token
                            if (!(("top".equals(token) || "thing".equals(token)) && !node.hasParent())) {
                                wnSense = linguisticOracle.getSenses(token);
                            } else {
                                wnSense = new ArrayList<>();
                            }
                            if (0 == wnSense.size()) {
                                List<String> newTokens = complexWordsRecognition(token);
                                if (0 < newTokens.size()) {
                                    tokensOfNodeLabel.remove(tokensOfNodeLabel.size() - 1);
                                    tokensOfNodeLabel.add(newTokens.get(0));
                                    wnSense = linguisticOracle.getSenses(newTokens.get(0));
                                    tokens.remove(i);
                                    tokens.add(i, newTokens.get(0));
                                    for (int j = 1; j < newTokens.size(); j++) {
                                        String s = newTokens.get(j);
                                        tokens.add(i + j, s);
                                    }
                                }
                            }
                            List<String> lemmas = linguisticOracle.getBaseForms(token);
                            String lemma = token;
                            if (0 < lemmas.size()) {
                                lemma = lemmas.get(0);
                            }

                            // create atomic node of label
                            IAtomicConceptOfLabel ACoL = createACoL(node, id_tok, token, lemma);
                            // mark id as meaningful
                            meaningfulTokens = meaningfulTokens + id_tok + " ";
                            // if there no WN senses
                            if (0 == wnSense.size() && !(("top".equals(labelOfNode) || "thing".equals(labelOfNode)) && !node.hasParent())) {
                                unrecognizedWords.add(token);
                            }
                            // add senses to ACoL
                            for (ISense sense : wnSense) {
                                ACoL.addSense(sense);
                            }
                            isEmpty = false;
                        }
                        id_tok++;
                    }
                }
            }

            if (isEmpty) {
                String token = labelOfNode.replaceAll(" ", "_");
                // add to list of processed labels
                tokensOfNodeLabel.add(token);
                // create atomic node of label
                createACoL(node, id_tok, token, token);
                // to token ids
                meaningfulTokens = meaningfulTokens + id_tok + " ";
            }
            // build complex formula of a node
            buildComplexConcept(node, tokensOfNodeLabel, meaningfulTokens);
            node.getNodeData().setIsPreprocessed(true);
        } catch (LinguisticOracleException e) {
            throw new ContextPreprocessorException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }
    }

    private IAtomicConceptOfLabel createACoL(INode node, int id, String token, String lemma) {
        IAtomicConceptOfLabel result = node.getNodeData().createACoL();
        result.setId(id);
        result.setToken(token);
        result.setLemma(lemma);
        node.getNodeData().addACoL(result);
        return result;
    }

    /**
     * Checks the token is meaningful or not.
     *
     * @param token the token
     * @return true if the token is meaningful
     */
    private boolean isTokenMeaningful(String token) {
        token = token.trim();
        return andWords.contains(token) || orWords.contains(token) || token.length() >= 3;
    }

    /**
     * Finds out if the input token is a complex word or not using WordNet. Tries to insert spaces and dash
     * between all characters and searches for the result to be in WordNet.
     *
     * @param token token
     * @return a list which contains parts of the complex word
     * @throws ContextPreprocessorException ContextPreprocessorException
     */
    private List<String> complexWordsRecognition(String token) throws ContextPreprocessorException {
        List<String> result = new ArrayList<>();
        try {
            List<ISense> senses = new ArrayList<>();
            int i = 0;
            String start = null;
            String end = null;
            String toCheck = null;
            boolean flag = false;
            boolean multiword = false;
            while ((i < token.length() - 1) && (0 == senses.size())) {
                i++;
                start = token.substring(0, i);
                end = token.substring(i, token.length());
                toCheck = start + ' ' + end;
                senses = linguisticOracle.getSenses(toCheck);
                if (0 == senses.size()) {
                    toCheck = start + '-' + end;
                    senses = linguisticOracle.getSenses(toCheck);
                }

                if (0 < senses.size()) {
                    multiword = true;
                    break;
                } else {
                    if ((start.length() > 3) && (end.length() > 3)) {
                        senses = linguisticOracle.getSenses(start);
                        if (0 < senses.size()) {
                            senses = linguisticOracle.getSenses(end);
                            if (0 < senses.size()) {
                                flag = true;
                                break;
                            }
                        }
                    }
                }
            }
            if (multiword) {
                result.add(toCheck);
                return result;
            }
            if (flag) {
                result.add(start);
                result.add(end);
                return result;
            }
            return result;
        } catch (LinguisticOracleException e) {
            throw new ContextPreprocessorException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }
    }

    /**
     * Constructs the logical formula for the complex concept of label.
     *
     * @param node              node to build complex concept
     * @param tokensOfNodeLabel a list of tokens in the node label
     * @param meaningfulTokens  identifiers of the meaningful tokens
     */
    private void buildComplexConcept(INode node, List<String> tokensOfNodeLabel, String meaningfulTokens) {
        // label of node
        String token;
        // List of ACoLs identifiers
        List<String> vec = new ArrayList<>();
        // formula for the complex concept
        StringBuilder formulaOfConcept = new StringBuilder();
        // logical connective
        String connective = " ";
        // brackets to add
        String bracket = "";
        // whether to insert brackets
        boolean insert;
        // how many left brackets do not have corresponding right ones
        int bracketsBalance = 0;
        // number of left brackets
        int leftBrackets = 0;
        // for each token of node label
        for (int i = 0; i < tokensOfNodeLabel.size(); i++) {
            token = (tokensOfNodeLabel.get(i));
            // If logical AND or OR
            if (andWords.contains(" " + token + " ") || orWords.contains(" " + token + " ")) {
                insert = false;
                // If non first token
                if (vec != null && vec.size() > 0) {
                    // construct formula
                    if (connective.isEmpty()) {
                        formulaOfConcept.append(" | ").append(bracket).append(encloseWithParentheses(vec));
                    } else {
                        formulaOfConcept.append(connective).append(bracket).append(encloseWithParentheses(vec));
                    }
                    insert = true;
                    connective = "";
                    bracket = "";
                    vec = new ArrayList<>();
                    leftBrackets = 0;
                }
                // If bracket
                if (token.equals("(") && bracketsBalance >= 0) {
                    connective = " & ";
                    bracket = "(";
                    bracketsBalance = bracketsBalance + 1;
                    leftBrackets = leftBrackets + 1;
                } else if (token.equals(")") && bracketsBalance > 0) {
                    if (insert) {
                        formulaOfConcept.append(")");
                    }
                    bracketsBalance = bracketsBalance - 1;
                } else {
                    connective = " | ";
                }
                // If logical not
            } else if (notWords.contains(" " + token + " ")) {
                if (vec != null && vec.size() > 0) {
                    formulaOfConcept.append(connective).append(encloseWithParentheses(vec));
                    vec = new ArrayList<>();
                    connective = "";
                }
                // What to add
                if (connective.contains("&") || connective.contains("|")) {
                    connective = connective + " ~ ";
                } else {
                    connective = " & ~ ";
                }
            } else {
                if (meaningfulTokens.contains(" " + i + " ")) {
                    // fill list with ACoL ids
                    vec.add((node.getNodeData().getId() + "_" + i));
                }
            }
        }
        // Dealing with first token of the node
        if (vec != null && vec.size() > 0) {
            //construct formula
            if (connective.contains("&") || connective.contains("|") || connective.equals(" ")) {
                formulaOfConcept.append(connective).append(bracket).append(encloseWithParentheses(vec));
            } else {
                formulaOfConcept.append(" & ").append(encloseWithParentheses(vec));
            }
            connective = "";
        } else {
            if (leftBrackets > 0) {
                bracketsBalance = bracketsBalance - leftBrackets;
            }
        }
        if (bracketsBalance > 0) {
            for (int i = 0; i < bracketsBalance; i++) {
                formulaOfConcept.append(")");
            }
        }
        // dealing with brackets
        String foc = formulaOfConcept.toString();
        foc = foc.replace('[', '(');
        foc = foc.replace(']', ')');
        foc = foc.replaceAll(", ", " & ");
        foc = foc.trim();
        if (foc.startsWith("&")) {
            StringTokenizer atoms = new StringTokenizer(foc, "&");
            foc = atoms.nextToken();
        }
        foc = foc.trim();
        if (foc.startsWith("|")) {
            StringTokenizer atoms = new StringTokenizer(foc, "|");
            foc = atoms.nextToken();
        }
        // bracket counters
        StringTokenizer open = new StringTokenizer(foc, "(", true);
        int openCount = 0;
        while (open.hasMoreTokens()) {
            String tmp = open.nextToken();
            if (tmp.equals("(")) {
                openCount++;
            }
        }
        StringTokenizer closed = new StringTokenizer(foc, ")", true);
        while (closed.hasMoreTokens()) {
            String tmp = closed.nextToken();
            if (tmp.equals(")")) {
                openCount--;
            }
        }
        formulaOfConcept = new StringBuilder(foc);
        if (openCount > 0) {
            for (int par = 0; par < openCount; par++) {
                formulaOfConcept.append(")");
            }
        }
        if (openCount < 0) {
            for (int par = 0; par < openCount; par++) {
                formulaOfConcept.insert(0, "(");
            }
        }
        // assign formula to the node
        node.getNodeData().setcLabFormula(formulaOfConcept.toString());
    }

    /**
     * Replaces punctuation by spaces.
     *
     * @param lemma input string
     * @return string with spaces in place of punctuation
     */
    private static String replacePunctuation(String lemma) {
        lemma = lemma.replace(",", " , ");
        lemma = lemma.replace('.', ' ');
//        lemma = lemma.replace('-', ' ');
        lemma = lemma.replace('\'', ' ');
        lemma = lemma.replace('(', ' ');
        lemma = lemma.replace(')', ' ');
        lemma = lemma.replace(':', ' ');
        lemma = lemma.replace(";", " ; ");
        return lemma;
    }

    private List<ISense> checkMW(String source, String target) throws ContextPreprocessorException {
        try {
            List<List<String>> mwEnds = linguisticOracle.getMultiwords(source);
            if (mwEnds != null) {
                for (List<String> strings : mwEnds) {
                    if (extendedIndexOf(strings, target, 0) > 0) {
                        return linguisticOracle.getSenses(source + " " + target);
                    }
                }
            }
            return new ArrayList<>();
        } catch (LinguisticOracleException e) {
            throw new ContextPreprocessorException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }
    }

    private void enrichSensesSets(IAtomicConceptOfLabel acol, List<ISense> senses) {
        for (ISense sense : senses) {
            acol.addSense(sense);
        }
    }

    /**
     * Finds multiwords in context.
     *
     * @param context data structure of input label
     * @return context with multiwords
     * @throws ContextPreprocessorException ContextPreprocessorException
     */
    private IContext findMultiwordsInContextStructure(IContext context) throws ContextPreprocessorException {
        for (Iterator<INode> i = context.getNodes(); i.hasNext(); ) {
            INode sourceNode = i.next();
            // sense disambiguation within the context structure
            // for all ACoLs in the source node
            for (Iterator<IAtomicConceptOfLabel> j = sourceNode.getNodeData().getACoLs(); j.hasNext(); ) {
                IAtomicConceptOfLabel synSource = j.next();
                // in all descendants and ancestors
                findMultiwordsAmong(sourceNode.getDescendants(), synSource);
                findMultiwordsAmong(sourceNode.getAncestors(), synSource);
            }
        }
        return context;
    }

    private void findMultiwordsAmong(Iterator<INode> i, IAtomicConceptOfLabel synSource) throws ContextPreprocessorException {
        while (i.hasNext()) {
            INode targetNode = i.next();
            for (Iterator<IAtomicConceptOfLabel> k = targetNode.getNodeData().getACoLs(); k.hasNext(); ) {
                IAtomicConceptOfLabel synTarget = k.next();
                List<ISense> wnSenses = checkMW(synSource.getLemma(), synTarget.getLemma());
                enrichSensesSets(synSource, wnSenses);
                enrichSensesSets(synTarget, wnSenses);
            }
        }
    }


    /**
     * Eliminates the senses which do not suit to overall context meaning. Filters senses in two steps:
     * - filtering within node label
     * - filtering within context
     *
     * @param context context to perform sense filtering
     * @throws SenseMatcherException SenseMatcherException
     */
    private void senseFiltering(IContext context) throws SenseMatcherException {
        HashMap<IAtomicConceptOfLabel, List<ISense>> refinedSenses = new HashMap<>();

        for (Iterator<INode> i = context.getNodes(); i.hasNext(); ) {
            INode sourceNode = i.next();
            // if node is complex
            if (1 < sourceNode.getNodeData().getACoLCount()) {
                // for each ACoL in the node
                for (Iterator<IAtomicConceptOfLabel> j = sourceNode.getNodeData().getACoLs(); j.hasNext(); ) {
                    IAtomicConceptOfLabel sourceACoL = j.next();
                    // compare with all the other ACoLs in the node
                    for (Iterator<IAtomicConceptOfLabel> k = sourceNode.getNodeData().getACoLs(); k.hasNext(); ) {
                        IAtomicConceptOfLabel targetACoL = k.next();
                        if (!targetACoL.equals(sourceACoL)) {
                            // for each sense in source ACoL
                            for (Iterator<ISense> s = sourceACoL.getSenses(); s.hasNext(); ) {
                                ISense sourceSense = s.next();
                                // for each sense in target ACoL
                                for (Iterator<ISense> t = targetACoL.getSenses(); t.hasNext(); ) {
                                    ISense targetSense = t.next();
                                    if (senseMatcher.isSourceSynonymTarget(sourceSense, targetSense) ||
                                            senseMatcher.isSourceLessGeneralThanTarget(sourceSense, targetSense) ||
                                            senseMatcher.isSourceMoreGeneralThanTarget(sourceSense, targetSense)) {
                                        addToRefinedSenses(refinedSenses, sourceACoL, sourceSense);
                                        addToRefinedSenses(refinedSenses, targetACoL, targetSense);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            // sense disambiguation within the context structure
            // for all ACoLs in the source node
            for (Iterator<IAtomicConceptOfLabel> j = sourceNode.getNodeData().getACoLs(); j.hasNext(); ) {
                IAtomicConceptOfLabel sourceACoL = j.next();
                if (!refinedSenses.containsKey(sourceACoL)) {
                    for (Iterator<ISense> s = sourceACoL.getSenses(); s.hasNext(); ) {
                        ISense sourceSense = s.next();
                        // for all target nodes (ancestors and descendants)
                        senseFilteringAmong(sourceNode.getDescendants(), sourceSense, sourceACoL, refinedSenses);
                        senseFilteringAmong(sourceNode.getAncestors(), sourceSense, sourceACoL, refinedSenses);
                    }
                }
            }
        }

        // Loop on senses of the all concepts and assign to them
        // senses mark as refined on the previous step
        // If there are no refined senses save the original ones
        for (Iterator<INode> i = context.getNodes(); i.hasNext(); ) {
            for (Iterator<IAtomicConceptOfLabel> j = i.next().getNodeData().getACoLs(); j.hasNext(); ) {
                IAtomicConceptOfLabel acol = j.next();
                List<ISense> refined = refinedSenses.get(acol);
                if (null != refined) {
                    while (0 < acol.getSenseCount()) {
                        acol.removeSense(0);
                    }
                    for (ISense sense : refined) {
                        acol.addSense(sense);
                    }
                }
            }
        }
    }

    private void addToRefinedSenses(HashMap<IAtomicConceptOfLabel, List<ISense>> refinedSenses, IAtomicConceptOfLabel acol, ISense sense) {
        List<ISense> senses = refinedSenses.get(acol);
        if (null == senses) {
            senses = new ArrayList<>();
        }
        senses.add(sense);
        refinedSenses.put(acol, senses);
    }

    private void senseFilteringAmong(Iterator<INode> i, ISense sourceSense, IAtomicConceptOfLabel sourceACoL, HashMap<IAtomicConceptOfLabel, List<ISense>> refinedSenses) throws SenseMatcherException {
        while (i.hasNext()) {
            INode targetNode = i.next();
            for (Iterator<IAtomicConceptOfLabel> k = targetNode.getNodeData().getACoLs(); k.hasNext(); ) {
                IAtomicConceptOfLabel targetACoL = k.next();
                if (null == refinedSenses.get(targetACoL)) {
                    for (Iterator<ISense> t = targetACoL.getSenses(); t.hasNext(); ) {
                        ISense targetSense = t.next();
                        // Check whether each sense not synonym or more general, less general then the senses of
                        // the ancestors and descendants of the node in context hierarchy
                        if ((senseMatcher.isSourceSynonymTarget(sourceSense, targetSense)) ||
                                (senseMatcher.isSourceLessGeneralThanTarget(sourceSense, targetSense)) ||
                                (senseMatcher.isSourceMoreGeneralThanTarget(sourceSense, targetSense))) {
                            addToRefinedSenses(refinedSenses, sourceACoL, sourceSense);
                            addToRefinedSenses(refinedSenses, targetACoL, targetSense);
                        }
                    }
                }
            }
        }
    }

    /**
     * Checks whether input string contains a number or not.
     *
     * @param in1 input string
     * @return false if it contains a number
     */
    private boolean isNumber(String in1) {
        //noinspection LoopStatementThatDoesntLoop
        for (StringTokenizer stringTokenizer = new StringTokenizer(in1, numberCharacters); stringTokenizer.hasMoreTokens(); ) {
            return false;
        }
        return true;
    }

    /**
     * An extension of the list indexOf method which uses approximate comparison of the words as
     * elements of the List.
     *
     * @param vec      list of strings
     * @param str      string to search
     * @param init_pos start position
     * @return position
     * @throws ContextPreprocessorException ContextPreprocessorException
     */
    private int extendedIndexOf(List<String> vec, String str, int init_pos) throws ContextPreprocessorException {
        try {
            // for all words in the input list starting from init_pos
            for (int i = init_pos; i < vec.size(); i++) {
                String vel = vec.get(i);
                // try syntactic
                if (vel.equals(str)) {
                    return i;
                } else if (vel.indexOf(str) == 0) {
                    // and semantic comparison
                    if (linguisticOracle.isEqual(vel, str)) {
                        vec.add(i, str);
                        vec.remove(i + 1);
                        return i;
                    }
                }
            }
            return -1;
        } catch (LinguisticOracleException e) {
            throw new ContextPreprocessorException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }
    }

    /**
     * Takes as an input a list of words and returns the list consisting of the multiwords
     * which are in WN and can be derived from the input
     * <p/>
     * For example having [Earth, and, Atmospheric, Sciences] as the input returns
     * [Earth Sciences, and, Atmospheric, Sciences] because Earth Sciences is a WN concept
     * and Atmospheric Sciences is not a WN concept
     *
     * @param tokens input tokens
     * @return a list which contains multiwords
     * @throws ContextPreprocessorException ContextPreprocessorException
     */
    private ArrayList<String> multiwordRecognition(ArrayList<String> tokens) throws ContextPreprocessorException {
        String subLemma;
        Map<String, List<Integer>> is_token_in_multiword = new HashMap<>();
        for (int i = 0; i < tokens.size(); i++) {
            subLemma = tokens.get(i);
            if ((!andWords.contains(subLemma)) || (!orWords.contains(subLemma))) {
                // if there a multiword starting with a sublemma
                List<List<String>> entries;
                try {
                    entries = linguisticOracle.getMultiwords(subLemma);
                } catch (LinguisticOracleException e) {
                    throw new ContextPreprocessorException(e.getMessage(), e);
                }
                if (null != entries) {
                    for (List<String> mweTail : entries) {
                        boolean flag = false;
                        int co = 0;
                        // at the end co is needed to move pointer for the cases like
                        // Clupea harengus with mw Clupea harengus harengus
                        while ((co < mweTail.size()) && (extendedIndexOf(tokens, mweTail.get(co), co) > i + co)) {
                            flag = true;
                            co++;
                        }
                        if ((co > mweTail.size() - 1) && (flag)) {
                            ArrayList<Integer> positions = new ArrayList<>();
                            int word_pos = tokens.indexOf(subLemma);
                            if (word_pos == -1) {
                                break;
                            }
                            int multiword_pos = word_pos;
                            positions.add(word_pos);
                            boolean cont = true;
                            boolean connectives_precedence = false;
                            int and_pos = -1;
                            for (String tok : mweTail) {
                                int old_pos = word_pos;
                                word_pos = tokens.subList(old_pos + 1, tokens.size()).indexOf(tok) + old_pos + 1;
                                if (word_pos == -1) {
                                    word_pos = extendedIndexOf(tokens, tok, old_pos);
                                    if (word_pos == -1) {
                                        break;
                                    }
                                }
                                if (word_pos - old_pos > 1) {
                                    cont = false;
                                    for (int r = old_pos + 1; r < word_pos; r++) {
                                        if (andWords.contains(tokens.get(r)) || orWords.contains(tokens.get(r))) {
                                            and_pos = r;
                                            connectives_precedence = true;
                                        } else {
                                            //connectives_precedence = false;
                                        }
                                    }
                                }
                                positions.add(word_pos);
                            }
                            int removed_tokens_index_correction = 0;
                            if (cont) {
                                String multiword = "";
                                for (Integer integer : positions) {
                                    int pos = integer - removed_tokens_index_correction;
                                    multiword = multiword + tokens.get(pos) + " ";
                                    tokens.remove(pos);
                                    removed_tokens_index_correction++;
                                }
                                multiword = multiword.substring(0, multiword.length() - 1);
                                tokens.add(multiword_pos, multiword);
                            } else {
                                if (connectives_precedence) {
                                    if (and_pos > multiword_pos) {
                                        String multiword = "";
                                        int word_distance = positions.get(positions.size() - 1) - positions.get(0);
                                        for (Integer integer : positions) {
                                            int pos = integer - removed_tokens_index_correction;
                                            if (is_token_in_multiword.get(tokens.get(pos)) == null) {
                                                ArrayList<Integer> toAdd = new ArrayList<>();
                                                toAdd.add(1);
                                                toAdd.add(word_distance - 1);
                                                is_token_in_multiword.put(tokens.get(pos), toAdd);
                                            } else {
                                                List<Integer> toAdd = is_token_in_multiword.get(tokens.get(pos));
                                                int tmp = toAdd.get(0) + 1;
                                                toAdd.remove(0);
                                                toAdd.add(0, tmp);
                                                is_token_in_multiword.put(tokens.get(pos), toAdd);
                                            }
                                            multiword = multiword + tokens.get(pos) + " ";
                                        }
                                        multiword = multiword.substring(0, multiword.length() - 1);
                                        tokens.remove(multiword_pos);
                                        tokens.add(multiword_pos, multiword);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        ArrayList<String> tmp = new ArrayList<>();
        for (String s : tokens) {
            if (is_token_in_multiword.get(s) == null) {
                tmp.add(s);
            } else {
                List<Integer> toAdd = is_token_in_multiword.get(s);
                int dist_wo_ands_ors = toAdd.get(0);
                int multiword_participation = toAdd.get(1);
                if (dist_wo_ands_ors != multiword_participation) {
                    tmp.add(s);
                }
            }
        }
        return tmp;
    }

    /**
     * Encloses list elements with parenthesis in such a way that operation is always binary.
     * This is a workaround for AIMA limitation.
     * <p/>
     * [n1.0] becomes [n1.0]
     * [n1.0, n2.0] becomes [n1.0, n2.0]
     * [n1.0, n2.0, n3.0] becomes [[n1.0], [n2.0, n3.0]]
     * [n1.0, n2.0, n3.0, n4.0] becomes [[n1.0, n2.0], [n3.0, n4.0]]
     * [n1.0, n2.0, n3.0, n4.0, n5.0] becomes [[n1.0, n2.0], [[n3.0], [n4.0, n5.0]]]
     *
     * @param vec vector of propositional logic items (maximum five elements)
     * @return the same formula of the input logic items with brackets
     */
    public static String encloseWithParentheses(List<String> vec) {
        if (vec.size() == 1) {
            return "[" + vec.get(0) + "]";
        } else if (vec.size() == 2) {
            return "[" + vec.get(0) + ", " + vec.get(1) + "]";
        } else {
            return "["
                    + encloseWithParentheses(vec.subList(0, (vec.size() / 2)))
                    + ", "
                    + encloseWithParentheses(vec.subList((vec.size() / 2), vec.size()))
                    + "]";
        }
    }
}