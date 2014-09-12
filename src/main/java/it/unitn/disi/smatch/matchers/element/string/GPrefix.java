package it.unitn.disi.smatch.matchers.element.string;

import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.matchers.element.IStringBasedElementLevelSemanticMatcher;

import java.util.HashMap;

/**
 * Implements GPrefix matcher.
 * Tries to use morphological knowledge (suffixes) to enhance relations returned.
 * It is called GPrefix because it matches words with equal prefixes, which differ in suffixes only.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class GPrefix implements IStringBasedElementLevelSemanticMatcher {

    //suffix -> relation
    //based on http://en.wiktionary.org/wiki/Appendix:Suffixes:English
    //in most cases = left
    //however may be > here in general case, because suffix specifies the meaning
    //also here http://en.wikipedia.org/wiki/List_of_English_suffixes
    //but not considered (yet)

    private static final HashMap<String, Character> suffixes = new HashMap<>();

    static {
        suffixes.put("a", IMappingElement.EQUIVALENCE);
        suffixes.put("ability", IMappingElement.EQUIVALENCE);
        suffixes.put("able", IMappingElement.EQUIVALENCE);
        suffixes.put("ably", IMappingElement.EQUIVALENCE);
        suffixes.put("ac", IMappingElement.EQUIVALENCE);
        //in WM lots with spaces, others are unrelated
        //suffixes.put("acea", MatchManager.IDK);//   1. taxonomic superfamily of plants, algae and fungi
        //in WM lots with spaces
        suffixes.put("aceae", IMappingElement.EQUIVALENCE);
        suffixes.put("acean", IMappingElement.EQUIVALENCE);
        suffixes.put("aceous", IMappingElement.EQUIVALENCE);
        suffixes.put("ad", IMappingElement.EQUIVALENCE);
        suffixes.put("ade", IMappingElement.EQUIVALENCE);
        suffixes.put("aemia", IMappingElement.EQUIVALENCE);
        suffixes.put("age", IMappingElement.EQUIVALENCE);
        suffixes.put("agog", IMappingElement.EQUIVALENCE);
        suffixes.put("agogue", IMappingElement.EQUIVALENCE);
        suffixes.put("aholic", IMappingElement.EQUIVALENCE);
        suffixes.put("al", IMappingElement.EQUIVALENCE);
        suffixes.put("ales", IMappingElement.EQUIVALENCE);
        suffixes.put("algia", IMappingElement.EQUIVALENCE);
        suffixes.put("amine", IMappingElement.EQUIVALENCE);
        suffixes.put("an", IMappingElement.EQUIVALENCE);
        suffixes.put("ana", IMappingElement.EQUIVALENCE);
        suffixes.put("anae", IMappingElement.EQUIVALENCE);
        suffixes.put("ance", IMappingElement.EQUIVALENCE);
        suffixes.put("ancy", IMappingElement.EQUIVALENCE);
        suffixes.put("androus", IMappingElement.EQUIVALENCE);
        suffixes.put("andry", IMappingElement.EQUIVALENCE);
        suffixes.put("ane", IMappingElement.EQUIVALENCE);
        suffixes.put("ant", IMappingElement.EQUIVALENCE);
        suffixes.put("ar", IMappingElement.EQUIVALENCE);
        suffixes.put("arch", IMappingElement.EQUIVALENCE);
        suffixes.put("archy", IMappingElement.EQUIVALENCE);
        suffixes.put("ard", IMappingElement.EQUIVALENCE);
        suffixes.put("aria", IMappingElement.EQUIVALENCE);
        suffixes.put("arian", IMappingElement.EQUIVALENCE);
        suffixes.put("arium", IMappingElement.EQUIVALENCE);
        suffixes.put("art", IMappingElement.EQUIVALENCE);
        suffixes.put("ary", IMappingElement.EQUIVALENCE);
        suffixes.put("ase", IMappingElement.EQUIVALENCE);
        suffixes.put("ate", IMappingElement.EQUIVALENCE);
        suffixes.put("athon", IMappingElement.EQUIVALENCE);
        suffixes.put("ation", IMappingElement.EQUIVALENCE);
        suffixes.put("ative", IMappingElement.EQUIVALENCE);
        suffixes.put("ator", IMappingElement.EQUIVALENCE);
        suffixes.put("atory", IMappingElement.EQUIVALENCE);
        suffixes.put("biont", IMappingElement.EQUIVALENCE);
        suffixes.put("biosis", IMappingElement.EQUIVALENCE);
        suffixes.put("blast", IMappingElement.EQUIVALENCE);
        suffixes.put("cade", IMappingElement.EQUIVALENCE);
        suffixes.put("caine", IMappingElement.EQUIVALENCE);
        suffixes.put("carp", IMappingElement.EQUIVALENCE);
        suffixes.put("carpic", IMappingElement.EQUIVALENCE);
        suffixes.put("carpous", IMappingElement.EQUIVALENCE);
        suffixes.put("cele", IMappingElement.EQUIVALENCE);
        suffixes.put("cene", IMappingElement.EQUIVALENCE);
        suffixes.put("centric", IMappingElement.EQUIVALENCE);
        suffixes.put("cephalic", IMappingElement.EQUIVALENCE);
        suffixes.put("cephalous", IMappingElement.EQUIVALENCE);
        suffixes.put("cephaly", IMappingElement.EQUIVALENCE);
        suffixes.put("chore", IMappingElement.EQUIVALENCE);
        suffixes.put("chory", IMappingElement.EQUIVALENCE);
        suffixes.put("chrome", IMappingElement.EQUIVALENCE);
        suffixes.put("cide", IMappingElement.EQUIVALENCE);
        suffixes.put("clinal", IMappingElement.EQUIVALENCE);
        suffixes.put("cline", IMappingElement.EQUIVALENCE);
        suffixes.put("clinic", IMappingElement.EQUIVALENCE);
        suffixes.put("coccus", IMappingElement.EQUIVALENCE);
        suffixes.put("coel", IMappingElement.EQUIVALENCE);
        suffixes.put("coele", IMappingElement.EQUIVALENCE);
        suffixes.put("colous", IMappingElement.EQUIVALENCE);
        suffixes.put("cracy", IMappingElement.EQUIVALENCE);
        suffixes.put("crat", IMappingElement.EQUIVALENCE);
        suffixes.put("cratic", IMappingElement.EQUIVALENCE);
        suffixes.put("cratical", IMappingElement.EQUIVALENCE);
        suffixes.put("cy", IMappingElement.EQUIVALENCE);
        suffixes.put("cyte", IMappingElement.EQUIVALENCE);
        suffixes.put("derm", IMappingElement.EQUIVALENCE);
        suffixes.put("derma", IMappingElement.EQUIVALENCE);
        suffixes.put("dermatous", IMappingElement.EQUIVALENCE);
        suffixes.put("dom", IMappingElement.EQUIVALENCE);
        suffixes.put("drome", IMappingElement.EQUIVALENCE);
        suffixes.put("dromous", IMappingElement.EQUIVALENCE);
        suffixes.put("eae", IMappingElement.EQUIVALENCE);
        suffixes.put("ectomy", IMappingElement.EQUIVALENCE);
        suffixes.put("ed", IMappingElement.EQUIVALENCE);
        suffixes.put("ee", IMappingElement.EQUIVALENCE);
        suffixes.put("eer", IMappingElement.EQUIVALENCE);
        suffixes.put("ein", IMappingElement.EQUIVALENCE);
        suffixes.put("eme", IMappingElement.EQUIVALENCE);
        suffixes.put("emia", IMappingElement.EQUIVALENCE);
        suffixes.put("en", IMappingElement.EQUIVALENCE);
        suffixes.put("ence", IMappingElement.EQUIVALENCE);
        suffixes.put("enchyma", IMappingElement.EQUIVALENCE);
        suffixes.put("ency", IMappingElement.EQUIVALENCE);
        suffixes.put("ene", IMappingElement.EQUIVALENCE);
        suffixes.put("ent", IMappingElement.EQUIVALENCE);
        suffixes.put("eous", IMappingElement.EQUIVALENCE);
        suffixes.put("er", IMappingElement.EQUIVALENCE);
        suffixes.put("ern", IMappingElement.EQUIVALENCE);
        suffixes.put("ergic", IMappingElement.EQUIVALENCE);
        suffixes.put("ergy", IMappingElement.EQUIVALENCE);
        suffixes.put("es", IMappingElement.EQUIVALENCE);
        suffixes.put("escence", IMappingElement.EQUIVALENCE);
        suffixes.put("escent", IMappingElement.EQUIVALENCE);
        suffixes.put("ese", IMappingElement.EQUIVALENCE);
        suffixes.put("esque", IMappingElement.EQUIVALENCE);
        suffixes.put("ess", IMappingElement.EQUIVALENCE);
        suffixes.put("est", IMappingElement.EQUIVALENCE);
        suffixes.put("et", IMappingElement.EQUIVALENCE);
        suffixes.put("eth", IMappingElement.EQUIVALENCE);
        suffixes.put("etic", IMappingElement.EQUIVALENCE);
        suffixes.put("ette", IMappingElement.EQUIVALENCE);
        suffixes.put("ey", IMappingElement.EQUIVALENCE);
        suffixes.put("facient", IMappingElement.EQUIVALENCE);
        suffixes.put("faction", IMappingElement.EQUIVALENCE);
        suffixes.put("fer", IMappingElement.EQUIVALENCE);
        suffixes.put("ferous", IMappingElement.EQUIVALENCE);
        suffixes.put("fic", IMappingElement.EQUIVALENCE);
        suffixes.put("fication", IMappingElement.EQUIVALENCE);
        suffixes.put("fid", IMappingElement.EQUIVALENCE);
        suffixes.put("florous", IMappingElement.EQUIVALENCE);
        suffixes.put("fold", IMappingElement.EQUIVALENCE);
        suffixes.put("foliate", IMappingElement.EQUIVALENCE);
        suffixes.put("foliolate", IMappingElement.EQUIVALENCE);
        suffixes.put("form", IMappingElement.EQUIVALENCE);
        suffixes.put("fuge", IMappingElement.EQUIVALENCE);
        suffixes.put("ful", IMappingElement.EQUIVALENCE);
        suffixes.put("fy", IMappingElement.EQUIVALENCE);
        suffixes.put("gamous", IMappingElement.EQUIVALENCE);
        suffixes.put("gamy", IMappingElement.EQUIVALENCE);
        suffixes.put("gate", IMappingElement.EQUIVALENCE);
        suffixes.put("gen", IMappingElement.EQUIVALENCE);
        suffixes.put("gene", IMappingElement.EQUIVALENCE);
        suffixes.put("genesis", IMappingElement.EQUIVALENCE);
        suffixes.put("genetic", IMappingElement.EQUIVALENCE);
        suffixes.put("genic", IMappingElement.EQUIVALENCE);
        suffixes.put("genous", IMappingElement.EQUIVALENCE);
        suffixes.put("geny", IMappingElement.EQUIVALENCE);
        suffixes.put("gnathous", IMappingElement.EQUIVALENCE);
        suffixes.put("gon", IMappingElement.EQUIVALENCE);
        suffixes.put("gony", IMappingElement.EQUIVALENCE);
        suffixes.put("gram", IMappingElement.EQUIVALENCE);
        suffixes.put("graph", IMappingElement.EQUIVALENCE);
        suffixes.put("grapher", IMappingElement.EQUIVALENCE);
        suffixes.put("graphy", IMappingElement.EQUIVALENCE);
        suffixes.put("gyne", IMappingElement.EQUIVALENCE);
        suffixes.put("gynous", IMappingElement.EQUIVALENCE);
        suffixes.put("gyny", IMappingElement.EQUIVALENCE);
        suffixes.put("hood", IMappingElement.EQUIVALENCE);
        suffixes.put("ia", IMappingElement.EQUIVALENCE);
        suffixes.put("ial", IMappingElement.EQUIVALENCE);
        suffixes.put("ian", IMappingElement.EQUIVALENCE);
        suffixes.put("iana", IMappingElement.EQUIVALENCE);
        suffixes.put("iasis", IMappingElement.EQUIVALENCE);
        suffixes.put("iatric", IMappingElement.EQUIVALENCE);
        suffixes.put("iatrics", IMappingElement.EQUIVALENCE);
        suffixes.put("iatry", IMappingElement.EQUIVALENCE);
        suffixes.put("ibility", IMappingElement.EQUIVALENCE);
        suffixes.put("ible", IMappingElement.EQUIVALENCE);
        suffixes.put("ic", IMappingElement.EQUIVALENCE);
        suffixes.put("ical", IMappingElement.EQUIVALENCE);
        suffixes.put("ically", IMappingElement.EQUIVALENCE);
        suffixes.put("ician", IMappingElement.EQUIVALENCE);
        suffixes.put("ics", IMappingElement.EQUIVALENCE);
        suffixes.put("id", IMappingElement.EQUIVALENCE);
        suffixes.put("idae", IMappingElement.EQUIVALENCE);
        suffixes.put("ide", IMappingElement.EQUIVALENCE);
        suffixes.put("ie", IMappingElement.EQUIVALENCE);
        suffixes.put("ify", IMappingElement.EQUIVALENCE);
        suffixes.put("ile", IMappingElement.EQUIVALENCE);
        suffixes.put("in", IMappingElement.EQUIVALENCE);
        suffixes.put("ina", IMappingElement.EQUIVALENCE);
        suffixes.put("inae", IMappingElement.EQUIVALENCE);
        suffixes.put("ine", IMappingElement.EQUIVALENCE);
        suffixes.put("ineae", IMappingElement.EQUIVALENCE);
        suffixes.put("ing", IMappingElement.EQUIVALENCE);
        suffixes.put("ini", IMappingElement.EQUIVALENCE);
        suffixes.put("ion", IMappingElement.EQUIVALENCE);
        suffixes.put("ious", IMappingElement.EQUIVALENCE);
        suffixes.put("isation", IMappingElement.EQUIVALENCE);
        suffixes.put("ise", IMappingElement.EQUIVALENCE);
        suffixes.put("ish", IMappingElement.EQUIVALENCE);
        suffixes.put("ism", IMappingElement.EQUIVALENCE);
        suffixes.put("ist", IMappingElement.EQUIVALENCE);
        suffixes.put("ite", IMappingElement.EQUIVALENCE);
        suffixes.put("itious", IMappingElement.EQUIVALENCE);
        suffixes.put("itis", IMappingElement.EQUIVALENCE);
        suffixes.put("ity", IMappingElement.EQUIVALENCE);
        suffixes.put("ium", IMappingElement.EQUIVALENCE);
        suffixes.put("ive", IMappingElement.EQUIVALENCE);
        suffixes.put("ix", IMappingElement.EQUIVALENCE);
        suffixes.put("ization", IMappingElement.EQUIVALENCE);
        suffixes.put("ize", IMappingElement.EQUIVALENCE);
        suffixes.put("i", IMappingElement.EQUIVALENCE);
        suffixes.put("kin", IMappingElement.EQUIVALENCE);
        suffixes.put("kinesis", IMappingElement.EQUIVALENCE);
        suffixes.put("kins", IMappingElement.EQUIVALENCE);
        suffixes.put("latry", IMappingElement.EQUIVALENCE);
        suffixes.put("lepry", IMappingElement.EQUIVALENCE);
        suffixes.put("less", IMappingElement.DISJOINT);//lacking something
        suffixes.put("let", IMappingElement.EQUIVALENCE);
        suffixes.put("like", IMappingElement.EQUIVALENCE);
        suffixes.put("ling", IMappingElement.EQUIVALENCE);
        suffixes.put("lite", IMappingElement.EQUIVALENCE);
        suffixes.put("lith", IMappingElement.EQUIVALENCE);
        suffixes.put("lithic", IMappingElement.EQUIVALENCE);
        suffixes.put("log", IMappingElement.EQUIVALENCE);
        suffixes.put("logue", IMappingElement.EQUIVALENCE);
        suffixes.put("logic", IMappingElement.EQUIVALENCE);
        suffixes.put("logical", IMappingElement.EQUIVALENCE);
        suffixes.put("logist", IMappingElement.EQUIVALENCE);
        suffixes.put("logy", IMappingElement.EQUIVALENCE);
        suffixes.put("ly", IMappingElement.EQUIVALENCE);
        suffixes.put("lyse", IMappingElement.EQUIVALENCE);
        /*
   1. decomposition or breakdown
   2. dissolving
   3. disintegration
         */
        suffixes.put("lysis", IMappingElement.DISJOINT);
        suffixes.put("lyte", IMappingElement.EQUIVALENCE);
        suffixes.put("lytic", IMappingElement.EQUIVALENCE);
        suffixes.put("lyze", IMappingElement.EQUIVALENCE);
        suffixes.put("mancy", IMappingElement.EQUIVALENCE);
        suffixes.put("mania", IMappingElement.EQUIVALENCE);
        suffixes.put("meister", IMappingElement.EQUIVALENCE);
        suffixes.put("ment", IMappingElement.EQUIVALENCE);
        suffixes.put("mer", IMappingElement.EQUIVALENCE);
        suffixes.put("mere", IMappingElement.EQUIVALENCE);
        suffixes.put("merous", IMappingElement.EQUIVALENCE);
        suffixes.put("meter", IMappingElement.EQUIVALENCE);
        suffixes.put("metric", IMappingElement.EQUIVALENCE);
        suffixes.put("metrics", IMappingElement.EQUIVALENCE);
        suffixes.put("metry", IMappingElement.EQUIVALENCE);
        suffixes.put("mo", IMappingElement.EQUIVALENCE);
        suffixes.put("morph", IMappingElement.EQUIVALENCE);
        suffixes.put("morphic", IMappingElement.EQUIVALENCE);
        suffixes.put("morphism", IMappingElement.EQUIVALENCE);
        suffixes.put("morphous", IMappingElement.EQUIVALENCE);
        suffixes.put("most", IMappingElement.EQUIVALENCE);
        suffixes.put("mycete", IMappingElement.EQUIVALENCE);
        suffixes.put("mycetes", IMappingElement.EQUIVALENCE);
        suffixes.put("mycetidae", IMappingElement.EQUIVALENCE);
        suffixes.put("mycin", IMappingElement.EQUIVALENCE);
        suffixes.put("mycota", IMappingElement.EQUIVALENCE);
        suffixes.put("mycotina", IMappingElement.EQUIVALENCE);
        suffixes.put("n", IMappingElement.EQUIVALENCE);//geographical africa - african
        suffixes.put("n't", IMappingElement.EQUIVALENCE);
        suffixes.put("nasty", IMappingElement.EQUIVALENCE);
        suffixes.put("ness", IMappingElement.EQUIVALENCE);
        suffixes.put("nik", IMappingElement.EQUIVALENCE);
        suffixes.put("nomy", IMappingElement.EQUIVALENCE);
        suffixes.put("o", IMappingElement.EQUIVALENCE);
        suffixes.put("ode", IMappingElement.EQUIVALENCE);
        suffixes.put("odon", IMappingElement.EQUIVALENCE);
        suffixes.put("odont", IMappingElement.EQUIVALENCE);
        suffixes.put("odontia", IMappingElement.EQUIVALENCE);
        suffixes.put("oholic", IMappingElement.EQUIVALENCE);
        suffixes.put("oic", IMappingElement.EQUIVALENCE);
        suffixes.put("oid", IMappingElement.EQUIVALENCE);
        suffixes.put("oidea", IMappingElement.EQUIVALENCE);
        suffixes.put("oideae", IMappingElement.EQUIVALENCE);
        suffixes.put("ol", IMappingElement.EQUIVALENCE);
        suffixes.put("ole", IMappingElement.EQUIVALENCE);
        suffixes.put("oma", IMappingElement.EQUIVALENCE);
        suffixes.put("ome", IMappingElement.EQUIVALENCE);
        suffixes.put("on", IMappingElement.EQUIVALENCE);
        suffixes.put("one", IMappingElement.EQUIVALENCE);
        suffixes.put("ont", IMappingElement.EQUIVALENCE);
        suffixes.put("onym", IMappingElement.EQUIVALENCE);
        suffixes.put("onymy", IMappingElement.EQUIVALENCE);
        suffixes.put("opia", IMappingElement.EQUIVALENCE);
        suffixes.put("opsida", IMappingElement.EQUIVALENCE);
        suffixes.put("opsis", IMappingElement.EQUIVALENCE);
        suffixes.put("opsy", IMappingElement.EQUIVALENCE);
        suffixes.put("or", IMappingElement.EQUIVALENCE);
        suffixes.put("ory", IMappingElement.EQUIVALENCE);
        suffixes.put("ose", IMappingElement.EQUIVALENCE);
        suffixes.put("osis", IMappingElement.EQUIVALENCE);
        suffixes.put("otic", IMappingElement.EQUIVALENCE);
        suffixes.put("otomy", IMappingElement.EQUIVALENCE);
        suffixes.put("ous", IMappingElement.EQUIVALENCE);
        suffixes.put("o", IMappingElement.EQUIVALENCE);
        suffixes.put("para", IMappingElement.EQUIVALENCE);
        suffixes.put("parous", IMappingElement.EQUIVALENCE);
        suffixes.put("path", IMappingElement.EQUIVALENCE);
        suffixes.put("pathy", IMappingElement.EQUIVALENCE);
        suffixes.put("ped", IMappingElement.EQUIVALENCE);
        suffixes.put("pede", IMappingElement.EQUIVALENCE);
        suffixes.put("penia", IMappingElement.EQUIVALENCE);
        suffixes.put("petal", IMappingElement.EQUIVALENCE);
        suffixes.put("phage", IMappingElement.EQUIVALENCE);
        suffixes.put("phagia", IMappingElement.EQUIVALENCE);
        suffixes.put("phagous", IMappingElement.EQUIVALENCE);
        suffixes.put("phagy", IMappingElement.EQUIVALENCE);
        suffixes.put("phane", IMappingElement.EQUIVALENCE);
        suffixes.put("phasia", IMappingElement.MORE_GENERAL);
        suffixes.put("phil", IMappingElement.MORE_GENERAL);
        suffixes.put("phile", IMappingElement.MORE_GENERAL);
        suffixes.put("philia", IMappingElement.MORE_GENERAL);//Used in the formation of nouns and adjectives meaning loving and friendly or love and friend
        suffixes.put("philiac", IMappingElement.MORE_GENERAL);
        suffixes.put("philic", IMappingElement.MORE_GENERAL);
        suffixes.put("philous", IMappingElement.MORE_GENERAL);
        /*
   1. Used to form nouns meaning a person having a fear of a specific thing.
          claustrophobe
   2. Used to form nouns meaning a person who hates a particular type of person (due to their fear of that type of person).
          homophobe
         */
        suffixes.put("phobe", IMappingElement.DISJOINT);
        suffixes.put("phobia", IMappingElement.MORE_GENERAL);
        suffixes.put("phobic", IMappingElement.MORE_GENERAL);
        /*
   1. a type of sound e.g. allophone
   2. something that makes a sound e.g. saxophone
   3. a speaker of a certain language e.g. Francophone
   4. part of some classical names, e.g., Persephone, Tisiphone
         */
        suffixes.put("phone", IMappingElement.MORE_GENERAL);
        suffixes.put("phony", IMappingElement.EQUIVALENCE);
        suffixes.put("phore", IMappingElement.EQUIVALENCE);
        suffixes.put("phoresis", IMappingElement.EQUIVALENCE);
        suffixes.put("phorous", IMappingElement.EQUIVALENCE);
        suffixes.put("phrenia", IMappingElement.EQUIVALENCE);
        suffixes.put("phyll", IMappingElement.EQUIVALENCE);
        suffixes.put("phyllous", IMappingElement.EQUIVALENCE);
        suffixes.put("phyceae", IMappingElement.EQUIVALENCE);
        suffixes.put("phycidae", IMappingElement.EQUIVALENCE);
        suffixes.put("phyta", IMappingElement.EQUIVALENCE);
        suffixes.put("phyte", IMappingElement.EQUIVALENCE);
        suffixes.put("phytina", IMappingElement.EQUIVALENCE);
        suffixes.put("plasia", IMappingElement.EQUIVALENCE);
        suffixes.put("plasm", IMappingElement.EQUIVALENCE);
        suffixes.put("plast", IMappingElement.EQUIVALENCE);
        suffixes.put("plastic", IMappingElement.EQUIVALENCE);
        suffixes.put("plasty", IMappingElement.EQUIVALENCE);
        suffixes.put("plegia", IMappingElement.EQUIVALENCE);
        suffixes.put("plex", IMappingElement.EQUIVALENCE);
        suffixes.put("ploid", IMappingElement.EQUIVALENCE);
        suffixes.put("pod", IMappingElement.EQUIVALENCE);
        suffixes.put("pode", IMappingElement.EQUIVALENCE);
        suffixes.put("podous", IMappingElement.EQUIVALENCE);
        suffixes.put("poieses", IMappingElement.EQUIVALENCE);
        suffixes.put("poietic", IMappingElement.EQUIVALENCE);
        suffixes.put("pter", IMappingElement.EQUIVALENCE);
        suffixes.put("rrhagia", IMappingElement.EQUIVALENCE);
        suffixes.put("rrhea", IMappingElement.EQUIVALENCE);
        suffixes.put("ric", IMappingElement.EQUIVALENCE);
        suffixes.put("ry", IMappingElement.EQUIVALENCE);
        suffixes.put("'s", IMappingElement.EQUIVALENCE);
        suffixes.put("s", IMappingElement.EQUIVALENCE);
        suffixes.put("scope", IMappingElement.MORE_GENERAL);//   1. instrument for viewing or examination
        suffixes.put("scopy", IMappingElement.EQUIVALENCE);
        suffixes.put("scribe", IMappingElement.EQUIVALENCE);
        suffixes.put("script", IMappingElement.EQUIVALENCE);
        suffixes.put("sect", IMappingElement.EQUIVALENCE);
        suffixes.put("sepalous", IMappingElement.EQUIVALENCE);
        suffixes.put("ship", IMappingElement.EQUIVALENCE);
        /*
   1. characterized by some specific condition or quality
          * Example: troublesome
   2. a group of a specified number of members
          * Example: foursome
         */
        suffixes.put("some", IMappingElement.MORE_GENERAL);
        suffixes.put("speak", IMappingElement.EQUIVALENCE);
        suffixes.put("sperm", IMappingElement.EQUIVALENCE);
        suffixes.put("sporous", IMappingElement.EQUIVALENCE);
        suffixes.put("st", IMappingElement.EQUIVALENCE);
        suffixes.put("stasis", IMappingElement.EQUIVALENCE);
        suffixes.put("stat", IMappingElement.EQUIVALENCE);
        suffixes.put("ster", IMappingElement.EQUIVALENCE);
        suffixes.put("stome", IMappingElement.EQUIVALENCE);
        suffixes.put("stomy", IMappingElement.EQUIVALENCE);
        suffixes.put("taxis", IMappingElement.EQUIVALENCE);
        suffixes.put("taxy", IMappingElement.EQUIVALENCE);
        suffixes.put("th", IMappingElement.EQUIVALENCE);
        suffixes.put("therm", IMappingElement.EQUIVALENCE);
        suffixes.put("thermal", IMappingElement.EQUIVALENCE);
        suffixes.put("thermic", IMappingElement.EQUIVALENCE);
        suffixes.put("thermy", IMappingElement.EQUIVALENCE);
        suffixes.put("thon", IMappingElement.EQUIVALENCE);
        suffixes.put("thymia", IMappingElement.EQUIVALENCE);
        suffixes.put("tion", IMappingElement.EQUIVALENCE);
        suffixes.put("tome", IMappingElement.EQUIVALENCE);
        suffixes.put("tomy", IMappingElement.EQUIVALENCE);
        suffixes.put("tonia", IMappingElement.EQUIVALENCE);
        suffixes.put("trichous", IMappingElement.EQUIVALENCE);
        suffixes.put("trix", IMappingElement.EQUIVALENCE);
        suffixes.put("tron", IMappingElement.EQUIVALENCE);
        suffixes.put("trophic", IMappingElement.EQUIVALENCE);
        suffixes.put("trophy", IMappingElement.EQUIVALENCE);
        suffixes.put("tropic", IMappingElement.EQUIVALENCE);
        suffixes.put("tropism", IMappingElement.EQUIVALENCE);
        suffixes.put("tropous", IMappingElement.EQUIVALENCE);
        suffixes.put("tropy", IMappingElement.EQUIVALENCE);
        suffixes.put("tude", IMappingElement.EQUIVALENCE);
        suffixes.put("ty", IMappingElement.EQUIVALENCE);
        suffixes.put("ular", IMappingElement.EQUIVALENCE);
        suffixes.put("ule", IMappingElement.EQUIVALENCE);
        suffixes.put("ure", IMappingElement.EQUIVALENCE);
        suffixes.put("urgy", IMappingElement.EQUIVALENCE);
        suffixes.put("uria", IMappingElement.EQUIVALENCE);
        suffixes.put("uronic", IMappingElement.EQUIVALENCE);
        suffixes.put("urous", IMappingElement.EQUIVALENCE);
        suffixes.put("valent", IMappingElement.EQUIVALENCE);
        suffixes.put("virile", IMappingElement.EQUIVALENCE);
        suffixes.put("vorous", IMappingElement.EQUIVALENCE);
        suffixes.put("ward", IMappingElement.EQUIVALENCE);
        suffixes.put("wards", IMappingElement.EQUIVALENCE);
        suffixes.put("ware", IMappingElement.EQUIVALENCE);
        suffixes.put("ways", IMappingElement.EQUIVALENCE);
        suffixes.put("wide", IMappingElement.EQUIVALENCE);
        suffixes.put("wise", IMappingElement.EQUIVALENCE);
        suffixes.put("worthy", IMappingElement.EQUIVALENCE);
        suffixes.put("xor", IMappingElement.EQUIVALENCE);
        suffixes.put("y", IMappingElement.EQUIVALENCE);
        suffixes.put("yl", IMappingElement.EQUIVALENCE);
        suffixes.put("yne", IMappingElement.EQUIVALENCE);
        suffixes.put("zoic", IMappingElement.EQUIVALENCE);
        suffixes.put("zoon", IMappingElement.EQUIVALENCE);
        suffixes.put("zygous", IMappingElement.EQUIVALENCE);
        suffixes.put("zyme", IMappingElement.EQUIVALENCE);

        //"roots"
        //e.g. fish is more general than parrot-fish
        //or it is a fish? :)
        suffixes.put("fish", IMappingElement.MORE_GENERAL);
        suffixes.put("fish's", IMappingElement.MORE_GENERAL);
        suffixes.put("fishes", IMappingElement.MORE_GENERAL);
        suffixes.put("way", IMappingElement.MORE_GENERAL);
        suffixes.put("ways", IMappingElement.MORE_GENERAL);
        suffixes.put("bird", IMappingElement.MORE_GENERAL);
        suffixes.put("bird's", IMappingElement.MORE_GENERAL);
        suffixes.put("birds", IMappingElement.MORE_GENERAL);
        suffixes.put("room", IMappingElement.MORE_GENERAL);
        suffixes.put("rooms", IMappingElement.MORE_GENERAL);
        suffixes.put("grass", IMappingElement.MORE_GENERAL);
        suffixes.put("grasses", IMappingElement.MORE_GENERAL);
        suffixes.put("boat", IMappingElement.MORE_GENERAL);
        suffixes.put("boats", IMappingElement.MORE_GENERAL);
        suffixes.put("bush", IMappingElement.MORE_GENERAL);
        suffixes.put("bushes", IMappingElement.MORE_GENERAL);
        suffixes.put("bone", IMappingElement.MORE_GENERAL);
        suffixes.put("bones", IMappingElement.MORE_GENERAL);
        suffixes.put("band", IMappingElement.MORE_GENERAL);
        suffixes.put("bands", IMappingElement.MORE_GENERAL);
        suffixes.put("cake", IMappingElement.MORE_GENERAL);
        suffixes.put("cakes", IMappingElement.MORE_GENERAL);
        suffixes.put("shop", IMappingElement.MORE_GENERAL);
        suffixes.put("shops", IMappingElement.MORE_GENERAL);
        suffixes.put("mill", IMappingElement.MORE_GENERAL);
        suffixes.put("mills", IMappingElement.MORE_GENERAL);
        suffixes.put("paper", IMappingElement.MORE_GENERAL);
        suffixes.put("papers", IMappingElement.MORE_GENERAL);
        suffixes.put("worship", IMappingElement.MORE_GENERAL);
        suffixes.put("snake", IMappingElement.MORE_GENERAL);
        suffixes.put("snake's", IMappingElement.MORE_GENERAL);
        suffixes.put("snakes", IMappingElement.MORE_GENERAL);
        suffixes.put("road", IMappingElement.MORE_GENERAL);
        suffixes.put("roads", IMappingElement.MORE_GENERAL);
        suffixes.put("hound", IMappingElement.MORE_GENERAL);
        suffixes.put("hound's", IMappingElement.MORE_GENERAL);
        suffixes.put("hounds", IMappingElement.MORE_GENERAL);
        suffixes.put("care", IMappingElement.MORE_GENERAL);
        suffixes.put("cares", IMappingElement.MORE_GENERAL);
        suffixes.put("virus", IMappingElement.MORE_GENERAL);
        suffixes.put("virus'", IMappingElement.MORE_GENERAL);
        suffixes.put("viruses", IMappingElement.MORE_GENERAL);
        suffixes.put("storm", IMappingElement.MORE_GENERAL);
        suffixes.put("storms", IMappingElement.MORE_GENERAL);
        suffixes.put("sail", IMappingElement.MORE_GENERAL);
        suffixes.put("sail's", IMappingElement.MORE_GENERAL);
        suffixes.put("sails", IMappingElement.MORE_GENERAL);
        suffixes.put("boot", IMappingElement.MORE_GENERAL);
        suffixes.put("boots", IMappingElement.MORE_GENERAL);
        suffixes.put("bee", IMappingElement.MORE_GENERAL);
        suffixes.put("bee's", IMappingElement.MORE_GENERAL);
        suffixes.put("bees", IMappingElement.MORE_GENERAL);
        suffixes.put("ache", IMappingElement.MORE_GENERAL);
        suffixes.put("aches", IMappingElement.MORE_GENERAL);
        suffixes.put("wear", IMappingElement.MORE_GENERAL);
        suffixes.put("wears", IMappingElement.MORE_GENERAL);
        suffixes.put("tit", IMappingElement.MORE_GENERAL);
        suffixes.put("tits", IMappingElement.MORE_GENERAL);
        suffixes.put("tax", IMappingElement.MORE_GENERAL);
        suffixes.put("taxes", IMappingElement.MORE_GENERAL);
        suffixes.put("spoon", IMappingElement.MORE_GENERAL);
        suffixes.put("spoons", IMappingElement.MORE_GENERAL);
        suffixes.put("song", IMappingElement.MORE_GENERAL);
        suffixes.put("songs", IMappingElement.MORE_GENERAL);
        suffixes.put("builder", IMappingElement.MORE_GENERAL);
        suffixes.put("builder's", IMappingElement.MORE_GENERAL);
        suffixes.put("builders", IMappingElement.MORE_GENERAL);
        suffixes.put("vine", IMappingElement.MORE_GENERAL);
        suffixes.put("vines", IMappingElement.MORE_GENERAL);
        suffixes.put("saddle", IMappingElement.MORE_GENERAL);
        suffixes.put("saddles", IMappingElement.MORE_GENERAL);
        suffixes.put("plant", IMappingElement.MORE_GENERAL);
        suffixes.put("plants", IMappingElement.MORE_GENERAL);
        suffixes.put("knife", IMappingElement.MORE_GENERAL);
        suffixes.put("knives", IMappingElement.MORE_GENERAL);
        suffixes.put("frog", IMappingElement.MORE_GENERAL);
        suffixes.put("frog's", IMappingElement.MORE_GENERAL);
        suffixes.put("frogs", IMappingElement.MORE_GENERAL);
        suffixes.put("chop", IMappingElement.MORE_GENERAL);
        suffixes.put("chops", IMappingElement.MORE_GENERAL);
        suffixes.put("writer", IMappingElement.MORE_GENERAL);
        suffixes.put("writer's", IMappingElement.MORE_GENERAL);
        suffixes.put("writers", IMappingElement.MORE_GENERAL);
        suffixes.put("wright", IMappingElement.MORE_GENERAL);
        suffixes.put("wrights", IMappingElement.MORE_GENERAL);
        suffixes.put("person", IMappingElement.MORE_GENERAL);
        suffixes.put("person's", IMappingElement.MORE_GENERAL);
        suffixes.put("persons", IMappingElement.MORE_GENERAL);
        suffixes.put("owner", IMappingElement.MORE_GENERAL);
        suffixes.put("owner's", IMappingElement.MORE_GENERAL);
        suffixes.put("owners", IMappingElement.MORE_GENERAL);
        suffixes.put("mint", IMappingElement.MORE_GENERAL);
        suffixes.put("rack", IMappingElement.MORE_GENERAL);
        suffixes.put("racks", IMappingElement.MORE_GENERAL);
        suffixes.put("name", IMappingElement.MORE_GENERAL);
        suffixes.put("names", IMappingElement.MORE_GENERAL);
        suffixes.put("mast", IMappingElement.MORE_GENERAL);
        suffixes.put("in-law", IMappingElement.MORE_GENERAL);
        suffixes.put("fruit", IMappingElement.MORE_GENERAL);
        suffixes.put("fruits", IMappingElement.MORE_GENERAL);
        suffixes.put("pox", IMappingElement.MORE_GENERAL);
        suffixes.put("poxes", IMappingElement.MORE_GENERAL);
        suffixes.put("hide", IMappingElement.MORE_GENERAL);
        suffixes.put("force", IMappingElement.MORE_GENERAL);
        suffixes.put("forces", IMappingElement.MORE_GENERAL);
    }

    public char match(String str1, String str2) {
        char rel;

        if (str1 == null || str2 == null) {
            rel = IMappingElement.IDK;
        } else {
            if ((str1.length() > 3) && (str2.length() > 3)) {
                if (str1.equals(str2)) {
                    rel = IMappingElement.EQUIVALENCE;
                } else if (str1.startsWith(str2)) {
                    rel = matchPrefix(str1, str2);
                } else if (str2.startsWith(str1)) {
                    rel = matchPrefix(str2, str1);
                    rel = reverseRelation(rel);
                } else {
                    rel = IMappingElement.IDK;
                }
            } else {//if ((str1.length() > 3) && (str2.length() > 3)) {
                rel = IMappingElement.IDK;
            }
        }

        return rel;
    }

    /**
     * Computes relation with prefix matcher.
     *
     * @param str1 the source input
     * @param str2 the target input
     * @return synonym, more general, less general or IDK relation
     */
    private char matchPrefix(String str1, String str2) {
        //here always str1.startsWith(str2) colorless!color
        char rel = IMappingElement.IDK;
        int spacePos1 = str1.indexOf(' ');
        String suffix = str1.substring(str2.length());
        if (-1 < spacePos1 && !suffixes.containsKey(suffix)) {//check suffixes - pole vault=pole vaulter
            if (str2.length() == spacePos1) {//plant part<plant
                rel = IMappingElement.LESS_GENERAL;
            } else {//plant part<plan
                String left = str1.substring(0, spacePos1);
                char secondRel = match(left, str2);
                if (IMappingElement.MORE_GENERAL == secondRel ||
                        IMappingElement.EQUIVALENCE == secondRel) {
                    rel = IMappingElement.LESS_GENERAL;
                } else { //?,<,!
                    rel = secondRel;
                }
            }
        } else {
            //spelling: -tree and tree
            if (suffix.startsWith("-")) {
                suffix = suffix.substring(1);
            }
            if (suffix.endsWith("-") || suffix.endsWith(";") || suffix.endsWith(".") || suffix.endsWith(",") || suffix.endsWith("-")) {
                suffix = suffix.substring(0, suffix.length() - 1);
            }
            if (suffixes.containsKey(suffix)) {
                rel = suffixes.get(suffix);
                rel = reverseRelation(rel);
            }

            //another approximation = Gversion4
//            if (rel == MatchManager.LESS_GENERAL || rel == MatchManager.MORE_GENERAL) {
//                rel = MatchManager.EQUIVALENCE;
//            }
        }

        //filter = Gversion3
//        if (MatchManager.LESS_GENERAL == rel || MatchManager.MORE_GENERAL == rel) {
//            rel = MatchManager.EQUIVALENCE;
//        }

        return rel;
    }

    private char reverseRelation(char rel) {
        char res = rel;
        if (rel == IMappingElement.MORE_GENERAL) {
            res = IMappingElement.LESS_GENERAL;
        }
        if (rel == IMappingElement.LESS_GENERAL) {
            res = IMappingElement.MORE_GENERAL;
        }
        return res;
    }
}