package it.unitn.disi.smatch.preprocessors;

import it.unitn.disi.smatch.async.AsyncTask;
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
public class DefaultContextPreprocessor extends BaseContextPreprocessor implements IAsyncContextPreprocessor {

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

    protected final ISenseMatcher senseMatcher;
    protected final ILinguisticOracle linguisticOracle;

    protected final String meaninglessWords;
    protected final String andWords;
    protected final String orWords;
    protected final String notWords;
    protected final String numberCharacters;

    public DefaultContextPreprocessor(ISenseMatcher senseMatcher, ILinguisticOracle linguisticOracle) {
        super(null);
        this.senseMatcher = senseMatcher;
        this.linguisticOracle = linguisticOracle;

        this.meaninglessWords = DEFAULT_MEANINGLESS_WORDS;
        this.andWords = DEFAULT_AND_WORDS;
        this.orWords = DEFAULT_OR_WORDS;
        this.notWords = DEFAULT_NOT_WORDS;
        this.numberCharacters = DEFAULT_NUMBER_CHARACTERS;
    }

    public DefaultContextPreprocessor(ISenseMatcher senseMatcher, ILinguisticOracle linguisticOracle, IContext context) {
        super(context);
        this.senseMatcher = senseMatcher;
        this.linguisticOracle = linguisticOracle;

        this.meaninglessWords = DEFAULT_MEANINGLESS_WORDS;
        this.andWords = DEFAULT_AND_WORDS;
        this.orWords = DEFAULT_OR_WORDS;
        this.notWords = DEFAULT_NOT_WORDS;
        this.numberCharacters = DEFAULT_NUMBER_CHARACTERS;

        setTotal(4 * context.nodesCount());
    }

    public DefaultContextPreprocessor(ISenseMatcher senseMatcher, ILinguisticOracle linguisticOracle,
                                      String meaninglessWords, String andWords,
                                      String orWords, String notWords, String numberCharacters) {
        super(null);
        this.senseMatcher = senseMatcher;
        this.linguisticOracle = linguisticOracle;

        this.meaninglessWords = meaninglessWords;
        this.andWords = andWords;
        this.orWords = orWords;
        this.notWords = notWords;
        this.numberCharacters = numberCharacters;
    }

    public DefaultContextPreprocessor(ISenseMatcher senseMatcher, ILinguisticOracle linguisticOracle,
                                      String meaninglessWords, String andWords, String orWords,
                                      String notWords, String numberCharacters, IContext context) {
        super(context);
        this.senseMatcher = senseMatcher;
        this.linguisticOracle = linguisticOracle;

        this.meaninglessWords = meaninglessWords;
        this.andWords = andWords;
        this.orWords = orWords;
        this.notWords = notWords;
        this.numberCharacters = numberCharacters;

        setTotal(4 * context.nodesCount());
    }

    /**
     * Performs all preprocessing procedures as follows:
     * - linguistic analysis (each lemma is associated with the set of senses taken from the oracle).
     * - sense filtering (elimination of irrelevant to context structure senses)
     *
     * @param context context to be preprocessed
     * @throws ContextPreprocessorException ContextPreprocessorException
     */
    @Override
    public void preprocess(IContext context) throws ContextPreprocessorException {
        
        setTotal(4 * context.nodesCount());        
        setProgress(0);
        
        Set<String> unrecognizedWords = new HashSet<>();

        context = buildCLabs(context, unrecognizedWords);
        context = findMultiwordsInContextStructure(context);
        senseFiltering(context);

        reportUnrecognizedWords(unrecognizedWords);
                
    }

    protected void reportUnrecognizedWords(Set<String> unrecognizedWords) {
        if (log.isDebugEnabled()) {
            log.debug("Unrecognized words: " + unrecognizedWords.size());
        }
        if (log.isTraceEnabled()) {
            Set<String> sortedWords = new TreeSet<>(unrecognizedWords);
            for (String unrecognizedWord : sortedWords) {
                log.trace("Unrecognized word: " + unrecognizedWord);
            }
        }
    }

    @Override
    public AsyncTask<Void, INode> asyncPreprocess(IContext context) {
        return new DefaultContextPreprocessor(senseMatcher, linguisticOracle,
                meaninglessWords, andWords, orWords, notWords, numberCharacters, context);
    }

    /**
     * Constructs cLabs for all nodes of the context.
     *
     * @param context           context of node which cLab to be build
     * @param unrecognizedWords unrecognized words
     * @return context with cLabs
     * @throws ContextPreprocessorException ContextPreprocessorException
     */
    protected IContext buildCLabs(IContext context, Set<String> unrecognizedWords) throws ContextPreprocessorException {
        for (Iterator<INode> i = context.nodeIterator(); i.hasNext(); ) {
            if (Thread.currentThread().isInterrupted()) {
                break;
            }

            processNode(i.next(), unrecognizedWords);

            progress();
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
            node.nodeData().setLabelFormula("");
            node.nodeData().setNodeFormula("");
            node.nodeData().getConcepts().clear();

            boolean isEmpty = true;
            String labelOfNode = node.nodeData().getName().trim();

            log.trace("preprocessing: " + labelOfNode);

            labelOfNode = replacePunctuation(labelOfNode);
            labelOfNode = labelOfNode.toLowerCase();
            List<ISense> wnSense = new ArrayList<>();
            if (!(("top".equals(labelOfNode) || "thing".equals(labelOfNode)) && !node.hasParent())
                    && (!meaninglessWords.contains(labelOfNode + " ")) && (isTokenMeaningful(labelOfNode))) {
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
                // add senses to ACoL
                ACoL.setSenses(wnSense);
                // to token ids
                meaningfulTokens = meaningfulTokens + id_tok + " ";
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
                        if (!andWords.contains(token) && !orWords.contains(token)
                                && !notWords.contains(token) && !hasNumber(token)) {
                            // get WN senses for token
                            if (!(("top".equals(token) || "thing".equals(token)) && !node.hasParent())) {
                                wnSense = linguisticOracle.getSenses(token);
                            } else {
                                wnSense = Collections.emptyList();
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
                            } else {
                                ACoL.setSenses(wnSense);
                            }
                            isEmpty = false;
                        }
                        id_tok++;
                    }
                }
            }

            if (isEmpty) {
                String token = labelOfNode.replace(' ', '_');
                // add to list of processed labels
                tokensOfNodeLabel.add(token);
                // create atomic node of label
                createACoL(node, id_tok, token, token);
                // to token ids
                meaningfulTokens = meaningfulTokens + id_tok + " ";
            }
            // build complex formula of a node
            buildComplexConcept(node, tokensOfNodeLabel, meaningfulTokens);
            node.nodeData().setIsPreprocessed(true);
        } catch (LinguisticOracleException e) {
            throw new ContextPreprocessorException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }
    }

    private IAtomicConceptOfLabel createACoL(INode node, int id, String token, String lemma) {
        IAtomicConceptOfLabel result = node.nodeData().createConcept();
        result.setId(id);
        result.setToken(token);
        result.setLemma(lemma);
        node.nodeData().getConcepts().add(result);
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
     * Finds out if the input token is a complex word or not using WordNet. Tries to insert space and dash
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
                        appendConjunction(formulaOfConcept.append(" | ").append(bracket), vec);
                    } else {
                        appendConjunction(formulaOfConcept.append(connective).append(bracket), vec);
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
                    appendConjunction(formulaOfConcept.append(connective), vec);
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
                    vec.add((node.nodeData().getId() + "_" + i));
                }
            }
        }
        // Dealing with first token of the node
        if (vec != null && vec.size() > 0) {
            //construct formula
            if (connective.contains("&") || connective.contains("|") || connective.equals(" ")) {
                appendConjunction(formulaOfConcept.append(connective).append(bracket), vec);
            } else {
                appendConjunction(formulaOfConcept.append(" & "), vec);
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
        node.nodeData().setLabelFormula(formulaOfConcept.toString());
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
            if (!acol.getSenses().contains(sense)) {
                acol.getSenses().add(sense);
            }
        }
    }

    /**
     * Finds multiwords in context.
     *
     * @param context data structure of input label
     * @return context with multiwords
     * @throws ContextPreprocessorException ContextPreprocessorException
     */
    protected IContext findMultiwordsInContextStructure(IContext context) throws ContextPreprocessorException {
        for (Iterator<INode> i = context.nodeIterator(); i.hasNext(); ) {
            if (Thread.currentThread().isInterrupted()) {
                break;
            }

            INode sourceNode = i.next();
            // sense disambiguation within the context structure
            // for all ACoLs in the source node
            for (IAtomicConceptOfLabel synSource : sourceNode.nodeData().getConcepts()) {
                // in all descendants and ancestors
                findMultiwordsAmong(sourceNode.descendantsIterator(), synSource);
                findMultiwordsAmong(sourceNode.ancestorsIterator(), synSource);
            }

            progress();
        }
        return context;
    }

    protected void findMultiwordsAmong(Iterator<INode> i, IAtomicConceptOfLabel synSource) throws ContextPreprocessorException {
        while (i.hasNext()) {
            INode targetNode = i.next();
            for (IAtomicConceptOfLabel synTarget : targetNode.nodeData().getConcepts()) {
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
     * @throws ContextPreprocessorException ContextPreprocessorException
     */
    protected void senseFiltering(IContext context) throws ContextPreprocessorException {
        HashMap<IAtomicConceptOfLabel, List<ISense>> refinedSenses = new HashMap<>();

        try {
            for (Iterator<INode> i = context.nodeIterator(); i.hasNext(); ) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }

                INode sourceNode = i.next();
                // if node is complex
                if (1 < sourceNode.nodeData().getConcepts().size()) {
                    // for each ACoL in the node
                    for (IAtomicConceptOfLabel sourceACoL : sourceNode.nodeData().getConcepts()) {
                        // compare with all the other ACoLs in the node
                        for (IAtomicConceptOfLabel targetACoL : sourceNode.nodeData().getConcepts()) {
                            if (!targetACoL.equals(sourceACoL)) {
                                // for each sense in source ACoL
                                for (ISense sourceSense : sourceACoL.getSenses()) {
                                    // for each sense in target ACoL
                                    for (ISense targetSense : targetACoL.getSenses()) {
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
                for (IAtomicConceptOfLabel sourceACoL : sourceNode.nodeData().getConcepts()) {
                    if (!refinedSenses.containsKey(sourceACoL)) {
                        for (ISense sourceSense : sourceACoL.getSenses()) {
                            // for all target nodes (ancestors and descendants)
                            senseFilteringAmong(sourceNode.descendantsIterator(), sourceSense, sourceACoL, refinedSenses);
                            senseFilteringAmong(sourceNode.ancestorsIterator(), sourceSense, sourceACoL, refinedSenses);
                        }
                    }
                }

                progress();
            }
        } catch (SenseMatcherException e) {
            throw new ContextPreprocessorException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }

        // Loop on senses of the all concepts and assign to them
        // senses marked as refined on the previous step
        // If there are no refined senses save the original ones
        for (Iterator<INode> i = context.nodeIterator(); i.hasNext(); ) {
            if (Thread.currentThread().isInterrupted()) {
                break;
            }

            List<IAtomicConceptOfLabel> concepts = i.next().nodeData().getConcepts();
            for (IAtomicConceptOfLabel acol : concepts) {
                List<ISense> refined = refinedSenses.get(acol);
                if (null != refined) {
                    acol.setSenses(refined);
                }
            }

            progress();
        }
    }

    protected void addToRefinedSenses(Map<IAtomicConceptOfLabel, List<ISense>> refinedSenses, IAtomicConceptOfLabel acol, ISense sense) {
        List<ISense> senses = refinedSenses.get(acol);
        if (null == senses) {
            senses = new ArrayList<>();
            refinedSenses.put(acol, senses);
        }
        if (-1 == senses.indexOf(sense)) {
            senses.add(sense);
        }
    }

    protected void senseFilteringAmong(Iterator<INode> i, ISense sourceSense, IAtomicConceptOfLabel sourceACoL,
                                       Map<IAtomicConceptOfLabel, List<ISense>> refinedSenses) throws ContextPreprocessorException {
        try {
            while (i.hasNext()) {
                INode targetNode = i.next();
                for (IAtomicConceptOfLabel targetACoL : targetNode.nodeData().getConcepts()) {
                    if (!refinedSenses.containsKey(targetACoL)) {
                        for (ISense targetSense : targetACoL.getSenses()) {
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
        } catch (SenseMatcherException e) {
            throw new ContextPreprocessorException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }
    }

    /**
     * Checks whether input string contains a number or not.
     *
     * @param input input string
     * @return true if it contains a number
     */
    private boolean hasNumber(String input) {
        for (int i = 0; i < numberCharacters.length(); i++) {
            if (-1 < input.indexOf(numberCharacters.charAt(i))) {
                return true;
            }
        }
        return false;
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

    private static void appendConjunction(StringBuilder b, List<String> vec) {
        for (int i = 0; i < vec.size(); i++) {
            if (i < (vec.size() - 1)) {
                b.append(vec.get(i)).append(" & ");
            } else {
                b.append(vec.get(i));
            }
        }
    }
}