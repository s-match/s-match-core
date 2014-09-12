package it.unitn.disi.smatch.matchers.element.string;

import it.unitn.disi.smatch.data.mappings.IMappingElement;
import it.unitn.disi.smatch.matchers.element.IStringBasedElementLevelSemanticMatcher;

import java.util.HashMap;

/**
 * Implements GSuffix matcher.
 * Tries to use morphological knowledge (prefixes) to enhance relations returned.
 * It is called GSuffix because it matches words with equal suffixes, which differ in prefixes only.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */

public class GSuffix implements IStringBasedElementLevelSemanticMatcher {

    //prefix -> relation
    //based on http://en.wiktionary.org/wiki/Appendix:Prefixes:English

    private static final HashMap<String, Character> prefixes = new HashMap<>();

    //for roots
    private static final HashMap<String, Character> suffixes = new HashMap<>();

    static {
        prefixes.put("a", IMappingElement.EQUIVALENCE);
        prefixes.put("ab", IMappingElement.LESS_GENERAL);
        prefixes.put("abs", IMappingElement.EQUIVALENCE);
        prefixes.put("ac", IMappingElement.EQUIVALENCE);
        prefixes.put("acet", IMappingElement.LESS_GENERAL);
        prefixes.put("aceto", IMappingElement.LESS_GENERAL);
        prefixes.put("acr", IMappingElement.EQUIVALENCE);
        prefixes.put("acro", IMappingElement.LESS_GENERAL);
        prefixes.put("actin", IMappingElement.EQUIVALENCE);
        prefixes.put("actino", IMappingElement.EQUIVALENCE);
        prefixes.put("ad", IMappingElement.EQUIVALENCE);
        prefixes.put("aden", IMappingElement.LESS_GENERAL);
        prefixes.put("adeno", IMappingElement.LESS_GENERAL);
        prefixes.put("ae", IMappingElement.LESS_GENERAL);
        prefixes.put("aer", IMappingElement.LESS_GENERAL);
        prefixes.put("aero", IMappingElement.LESS_GENERAL);
        prefixes.put("af", IMappingElement.EQUIVALENCE);
        prefixes.put("afro", IMappingElement.LESS_GENERAL);
        prefixes.put("ag", IMappingElement.EQUIVALENCE);
        prefixes.put("agr", IMappingElement.LESS_GENERAL);
        prefixes.put("agri", IMappingElement.LESS_GENERAL);
        prefixes.put("agro", IMappingElement.LESS_GENERAL);
        prefixes.put("al", IMappingElement.EQUIVALENCE);
        prefixes.put("allo", IMappingElement.DISJOINT);
        prefixes.put("ambi", IMappingElement.LESS_GENERAL);
        prefixes.put("amphi", IMappingElement.LESS_GENERAL);
        prefixes.put("an", IMappingElement.DISJOINT);
        prefixes.put("ana", IMappingElement.DISJOINT);
        prefixes.put("and", IMappingElement.EQUIVALENCE);
        prefixes.put("andr", IMappingElement.LESS_GENERAL);
        prefixes.put("andro", IMappingElement.LESS_GENERAL);
        prefixes.put("anemo", IMappingElement.LESS_GENERAL);
        prefixes.put("angio", IMappingElement.LESS_GENERAL);
        prefixes.put("anglo", IMappingElement.LESS_GENERAL);//was Anglo
        prefixes.put("ano", IMappingElement.LESS_GENERAL);
        prefixes.put("antho", IMappingElement.LESS_GENERAL);
        prefixes.put("anthrop", IMappingElement.LESS_GENERAL);
        prefixes.put("anthropo", IMappingElement.LESS_GENERAL);
        prefixes.put("ante", IMappingElement.LESS_GENERAL);
        prefixes.put("ant", IMappingElement.DISJOINT);//variation of anti
        prefixes.put("anth", IMappingElement.DISJOINT);//variation of anti
        prefixes.put("anti", IMappingElement.DISJOINT);
        prefixes.put("ap", IMappingElement.LESS_GENERAL);
        prefixes.put("apo", IMappingElement.LESS_GENERAL);
        prefixes.put("aqua", IMappingElement.LESS_GENERAL);
        prefixes.put("aque", IMappingElement.LESS_GENERAL);
        prefixes.put("aqui", IMappingElement.LESS_GENERAL);
        prefixes.put("arc", IMappingElement.DISJOINT);
        prefixes.put("arch", IMappingElement.LESS_GENERAL);
        prefixes.put("archi", IMappingElement.LESS_GENERAL);
        prefixes.put("archaeo", IMappingElement.LESS_GENERAL);
        prefixes.put("archeo", IMappingElement.LESS_GENERAL);
        prefixes.put("arithmo", IMappingElement.LESS_GENERAL);
        prefixes.put("arterio", IMappingElement.LESS_GENERAL);
        prefixes.put("arthr", IMappingElement.LESS_GENERAL);
        prefixes.put("arthro", IMappingElement.LESS_GENERAL);
        prefixes.put("astr", IMappingElement.LESS_GENERAL);
        prefixes.put("astro", IMappingElement.LESS_GENERAL);
        prefixes.put("atto", IMappingElement.LESS_GENERAL);//10^-18 from danish "atten"
        prefixes.put("audio", IMappingElement.LESS_GENERAL);
        prefixes.put("aut", IMappingElement.LESS_GENERAL);
        prefixes.put("auto", IMappingElement.LESS_GENERAL);
        prefixes.put("azo", IMappingElement.LESS_GENERAL);
        //prefixes.put("b", MatchManager.EQUIVALENCE);//was B
        prefixes.put("bacter", IMappingElement.EQUIVALENCE);
        prefixes.put("bacteri", IMappingElement.EQUIVALENCE);
        prefixes.put("bacterio", IMappingElement.EQUIVALENCE);
        prefixes.put("bar", IMappingElement.LESS_GENERAL);
        prefixes.put("baro", IMappingElement.LESS_GENERAL);
        prefixes.put("bathy", IMappingElement.LESS_GENERAL);//greek deep
        prefixes.put("be", IMappingElement.EQUIVALENCE);
        prefixes.put("benz", IMappingElement.LESS_GENERAL);
        prefixes.put("benzo", IMappingElement.LESS_GENERAL);
        prefixes.put("bi", IMappingElement.LESS_GENERAL);
        prefixes.put("bin", IMappingElement.LESS_GENERAL);
        prefixes.put("biblio", IMappingElement.LESS_GENERAL);
        prefixes.put("bio", IMappingElement.LESS_GENERAL);
        prefixes.put("blast", IMappingElement.LESS_GENERAL);
        prefixes.put("blasto", IMappingElement.LESS_GENERAL);
        prefixes.put("brachy", IMappingElement.LESS_GENERAL);
        prefixes.put("brady", IMappingElement.LESS_GENERAL);
        prefixes.put("brom", IMappingElement.LESS_GENERAL);
        prefixes.put("bromo", IMappingElement.LESS_GENERAL);
        prefixes.put("bronch", IMappingElement.LESS_GENERAL);
        prefixes.put("bronchi", IMappingElement.LESS_GENERAL);
        prefixes.put("broncho", IMappingElement.LESS_GENERAL);
        prefixes.put("bry", IMappingElement.EQUIVALENCE);
        prefixes.put("bryo", IMappingElement.EQUIVALENCE);
        prefixes.put("by", IMappingElement.LESS_GENERAL);
        prefixes.put("bye", IMappingElement.EQUIVALENCE);
        //prefixes.put("c", MatchManager.EQUIVALENCE);//was C
        prefixes.put("caco", IMappingElement.DISJOINT);//From Ancient Greek ????? (kakos), an adjective that means bad.
        prefixes.put("carb", IMappingElement.LESS_GENERAL);
        prefixes.put("carbo", IMappingElement.LESS_GENERAL);
        prefixes.put("cardi", IMappingElement.LESS_GENERAL);
        prefixes.put("cardio", IMappingElement.LESS_GENERAL);
        prefixes.put("cel", IMappingElement.LESS_GENERAL);
        prefixes.put("celo", IMappingElement.LESS_GENERAL);
        prefixes.put("cen", IMappingElement.LESS_GENERAL);
        prefixes.put("ceno", IMappingElement.LESS_GENERAL);
        prefixes.put("cent", IMappingElement.LESS_GENERAL);
        prefixes.put("centi", IMappingElement.LESS_GENERAL);
        prefixes.put("centr", IMappingElement.LESS_GENERAL);
        prefixes.put("centri", IMappingElement.LESS_GENERAL);
        prefixes.put("cephal", IMappingElement.LESS_GENERAL);//   1. (biology) relating to the brain or head
        prefixes.put("cephalo", IMappingElement.LESS_GENERAL);//   1. (biology) relating to the brain or head
        prefixes.put("chalco", IMappingElement.LESS_GENERAL);//copper, brass etc
        prefixes.put("cheiro", IMappingElement.LESS_GENERAL);
        prefixes.put("chem", IMappingElement.LESS_GENERAL);
        prefixes.put("chemi", IMappingElement.LESS_GENERAL);
        prefixes.put("chemico", IMappingElement.LESS_GENERAL);
        prefixes.put("chemo", IMappingElement.LESS_GENERAL);
        prefixes.put("chino", IMappingElement.LESS_GENERAL);//was Chino
        prefixes.put("chiro", IMappingElement.LESS_GENERAL);
        prefixes.put("chlor", IMappingElement.LESS_GENERAL);
        prefixes.put("chloro", IMappingElement.LESS_GENERAL);
        prefixes.put("choan", IMappingElement.LESS_GENERAL);
        prefixes.put("choano", IMappingElement.LESS_GENERAL);
        prefixes.put("chol", IMappingElement.LESS_GENERAL);
        prefixes.put("chole", IMappingElement.LESS_GENERAL);
        prefixes.put("christo", IMappingElement.LESS_GENERAL);//was Christo
        prefixes.put("chron", IMappingElement.LESS_GENERAL);
        prefixes.put("chrono", IMappingElement.LESS_GENERAL);
        prefixes.put("chrys", IMappingElement.LESS_GENERAL);
        prefixes.put("chryso", IMappingElement.LESS_GENERAL);
        prefixes.put("cine", IMappingElement.LESS_GENERAL);
        prefixes.put("circum", IMappingElement.LESS_GENERAL);
        prefixes.put("cis", IMappingElement.LESS_GENERAL);
        prefixes.put("co", IMappingElement.EQUIVALENCE);
        prefixes.put("coel", IMappingElement.LESS_GENERAL);
        prefixes.put("coelo", IMappingElement.LESS_GENERAL);
        prefixes.put("coen", IMappingElement.LESS_GENERAL);
        prefixes.put("coeno", IMappingElement.LESS_GENERAL);
        prefixes.put("col", IMappingElement.EQUIVALENCE);//con
        prefixes.put("com", IMappingElement.EQUIVALENCE);//con
        prefixes.put("copr", IMappingElement.LESS_GENERAL);
        prefixes.put("copro", IMappingElement.LESS_GENERAL);
        prefixes.put("con", IMappingElement.EQUIVALENCE);
        prefixes.put("contra", IMappingElement.DISJOINT);
        prefixes.put("cor", IMappingElement.EQUIVALENCE);//con
        prefixes.put("cosmo", IMappingElement.LESS_GENERAL);
        prefixes.put("counter", IMappingElement.DISJOINT);
        prefixes.put("cryo", IMappingElement.LESS_GENERAL);
        prefixes.put("crypto", IMappingElement.LESS_GENERAL);
        prefixes.put("cyan", IMappingElement.LESS_GENERAL);
        prefixes.put("cyano", IMappingElement.LESS_GENERAL);
        prefixes.put("cyber", IMappingElement.LESS_GENERAL);
        prefixes.put("cycl", IMappingElement.LESS_GENERAL);
        prefixes.put("cyclo", IMappingElement.LESS_GENERAL);
        prefixes.put("cyn", IMappingElement.LESS_GENERAL);
        prefixes.put("cyno", IMappingElement.LESS_GENERAL);
        prefixes.put("cyt", IMappingElement.LESS_GENERAL);
        prefixes.put("cyto", IMappingElement.LESS_GENERAL);
        prefixes.put("de", IMappingElement.DISJOINT);
        prefixes.put("dec", IMappingElement.LESS_GENERAL);
        prefixes.put("deca", IMappingElement.LESS_GENERAL);
        prefixes.put("deci", IMappingElement.LESS_GENERAL);
        prefixes.put("deka", IMappingElement.LESS_GENERAL);
        prefixes.put("demi", IMappingElement.LESS_GENERAL);
        prefixes.put("deoxy", IMappingElement.LESS_GENERAL);
        prefixes.put("deuter", IMappingElement.LESS_GENERAL);
        prefixes.put("deutero", IMappingElement.LESS_GENERAL);
        prefixes.put("di", IMappingElement.LESS_GENERAL);
        prefixes.put("dia", IMappingElement.LESS_GENERAL);
        //prefixes.put("di", MatchManager.LESS_GENERAL);
        prefixes.put("dichlor", IMappingElement.LESS_GENERAL);
        prefixes.put("dichloro", IMappingElement.LESS_GENERAL);
        prefixes.put("dinitro", IMappingElement.LESS_GENERAL);
        prefixes.put("dino", IMappingElement.LESS_GENERAL);
        prefixes.put("dipl", IMappingElement.LESS_GENERAL);
        prefixes.put("diplo", IMappingElement.LESS_GENERAL);
        prefixes.put("dis", IMappingElement.DISJOINT);
        //prefixes.put("di", MatchManager.LESS_GENERAL);
        prefixes.put("dodeca", IMappingElement.LESS_GENERAL);
        prefixes.put("dys", IMappingElement.DISJOINT);
        prefixes.put("eco", IMappingElement.LESS_GENERAL);
        prefixes.put("ecto", IMappingElement.LESS_GENERAL);
        prefixes.put("eigen", IMappingElement.LESS_GENERAL);
        prefixes.put("electro", IMappingElement.LESS_GENERAL);
        prefixes.put("em", IMappingElement.LESS_GENERAL);
        prefixes.put("en", IMappingElement.LESS_GENERAL);
        prefixes.put("endo", IMappingElement.LESS_GENERAL);
        prefixes.put("ennea", IMappingElement.LESS_GENERAL);
        prefixes.put("ento", IMappingElement.LESS_GENERAL);
        prefixes.put("epi", IMappingElement.LESS_GENERAL);
        prefixes.put("equi", IMappingElement.EQUIVALENCE);
        prefixes.put("ethno", IMappingElement.LESS_GENERAL);
        prefixes.put("eu", IMappingElement.LESS_GENERAL);
        prefixes.put("eur", IMappingElement.LESS_GENERAL);//Eur
        prefixes.put("euro", IMappingElement.LESS_GENERAL);//was Euro
        prefixes.put("ex", IMappingElement.LESS_GENERAL);
        prefixes.put("exa", IMappingElement.LESS_GENERAL);
        prefixes.put("exbi", IMappingElement.LESS_GENERAL);
        prefixes.put("exo", IMappingElement.LESS_GENERAL);
        prefixes.put("extra", IMappingElement.LESS_GENERAL);
        prefixes.put("femto", IMappingElement.LESS_GENERAL);
        prefixes.put("ferro", IMappingElement.LESS_GENERAL);
        prefixes.put("fluor", IMappingElement.LESS_GENERAL);
        prefixes.put("fluoro", IMappingElement.LESS_GENERAL);
        prefixes.put("for", IMappingElement.DISJOINT);//   1. Meaning from.    2. Meaning against.
        prefixes.put("fore", IMappingElement.DISJOINT);//   1. Meaning from.    2. Meaning against.
        prefixes.put("franco", IMappingElement.LESS_GENERAL);//was Franco
        prefixes.put("gastr", IMappingElement.LESS_GENERAL);
        prefixes.put("gastro", IMappingElement.LESS_GENERAL);
        prefixes.put("genito", IMappingElement.LESS_GENERAL);
        prefixes.put("geo", IMappingElement.LESS_GENERAL);
        prefixes.put("gibi", IMappingElement.LESS_GENERAL);
        prefixes.put("giga", IMappingElement.LESS_GENERAL);
        prefixes.put("geno", IMappingElement.LESS_GENERAL);
        prefixes.put("gymno", IMappingElement.LESS_GENERAL);
        prefixes.put("gyn", IMappingElement.LESS_GENERAL);
        prefixes.put("gyno", IMappingElement.LESS_GENERAL);
        prefixes.put("gyro", IMappingElement.LESS_GENERAL);
        prefixes.put("haem", IMappingElement.LESS_GENERAL);
        prefixes.put("haemat", IMappingElement.LESS_GENERAL);
        prefixes.put("haemo", IMappingElement.LESS_GENERAL);
        prefixes.put("hagi", IMappingElement.LESS_GENERAL);
        prefixes.put("hagio", IMappingElement.LESS_GENERAL);
        prefixes.put("half", IMappingElement.LESS_GENERAL);
        prefixes.put("hect", IMappingElement.LESS_GENERAL);
        prefixes.put("hecto", IMappingElement.LESS_GENERAL);
        prefixes.put("helio", IMappingElement.LESS_GENERAL);
        prefixes.put("hem", IMappingElement.LESS_GENERAL);
        prefixes.put("hemat", IMappingElement.LESS_GENERAL);
        prefixes.put("hemi", IMappingElement.LESS_GENERAL);
        prefixes.put("hemo", IMappingElement.LESS_GENERAL);
        prefixes.put("hendeca", IMappingElement.LESS_GENERAL);
        prefixes.put("hept", IMappingElement.LESS_GENERAL);
        prefixes.put("hepta", IMappingElement.LESS_GENERAL);
        prefixes.put("hetero", IMappingElement.LESS_GENERAL);
        prefixes.put("hex", IMappingElement.LESS_GENERAL);
        prefixes.put("hexa", IMappingElement.LESS_GENERAL);
        prefixes.put("hipp", IMappingElement.LESS_GENERAL);
        prefixes.put("hippo", IMappingElement.LESS_GENERAL);
        prefixes.put("hispano", IMappingElement.LESS_GENERAL);//was Hispano
        prefixes.put("hist", IMappingElement.LESS_GENERAL);
        prefixes.put("histio", IMappingElement.LESS_GENERAL);
        prefixes.put("histo", IMappingElement.LESS_GENERAL);
        prefixes.put("holo", IMappingElement.LESS_GENERAL);
        prefixes.put("homeo", IMappingElement.LESS_GENERAL);
        prefixes.put("homo", IMappingElement.LESS_GENERAL);
        prefixes.put("homoeo", IMappingElement.LESS_GENERAL);
        prefixes.put("hydro", IMappingElement.LESS_GENERAL);
        prefixes.put("hyper", IMappingElement.LESS_GENERAL);
        prefixes.put("hypno", IMappingElement.LESS_GENERAL);
        prefixes.put("hypo", IMappingElement.LESS_GENERAL);
        prefixes.put("il", IMappingElement.DISJOINT);//in
        prefixes.put("im", IMappingElement.DISJOINT);//in
        /*
, "not", or "in", "into".

[edit] Prefix

in-

   1. Used with certain words to reverse their meaning

          Note: Before certain letters, the n. changes to another letter:
              * il- before l, eg. illegal
              * im- before b, m. or p, eg. improper
              * ir- before r, eg. irresistible

         1. Added to adjectives to mean not

              inedible
              inaccurate

         1. Added to nouns to mean lacking or without

              incredulity
              ineptitude

   2. Prefixed to certain words to give the senses of in, into, towards, within.

          inbreed
          inbound

         */
        prefixes.put("in", IMappingElement.DISJOINT);
        prefixes.put("Indo", IMappingElement.LESS_GENERAL);
        prefixes.put("inter", IMappingElement.LESS_GENERAL);
        prefixes.put("intra", IMappingElement.LESS_GENERAL);
        prefixes.put("ir", IMappingElement.DISJOINT);//in
        prefixes.put("iso", IMappingElement.LESS_GENERAL);
        prefixes.put("italo", IMappingElement.LESS_GENERAL);//was Italo
        prefixes.put("kibi", IMappingElement.LESS_GENERAL);
        prefixes.put("kilo", IMappingElement.LESS_GENERAL);
        prefixes.put("lip", IMappingElement.LESS_GENERAL);
        prefixes.put("lipo", IMappingElement.LESS_GENERAL);
        prefixes.put("lith", IMappingElement.LESS_GENERAL);
        prefixes.put("litho", IMappingElement.LESS_GENERAL);
        prefixes.put("macro", IMappingElement.LESS_GENERAL);
        prefixes.put("mal", IMappingElement.DISJOINT);
        prefixes.put("mebi", IMappingElement.LESS_GENERAL);
        prefixes.put("mega", IMappingElement.LESS_GENERAL);
        prefixes.put("meso", IMappingElement.LESS_GENERAL);
        prefixes.put("meta", IMappingElement.MORE_GENERAL);//-metasearch<search
        prefixes.put("metro", IMappingElement.LESS_GENERAL);
        prefixes.put("micro", IMappingElement.LESS_GENERAL);
        prefixes.put("midi", IMappingElement.LESS_GENERAL);
        prefixes.put("milli", IMappingElement.LESS_GENERAL);
        prefixes.put("mini", IMappingElement.LESS_GENERAL);
        /*
mis-

   1. bad, badly, wrong, wrongly
   2. lack or failure
         */
        prefixes.put("mis", IMappingElement.DISJOINT);
        prefixes.put("miso", IMappingElement.LESS_GENERAL);
        prefixes.put("mono", IMappingElement.LESS_GENERAL);
        prefixes.put("multi", IMappingElement.MORE_GENERAL);//-genre>multi-genre
        prefixes.put("myria", IMappingElement.LESS_GENERAL);
        prefixes.put("myxo", IMappingElement.LESS_GENERAL);
        prefixes.put("nano", IMappingElement.LESS_GENERAL);
        prefixes.put("naso", IMappingElement.LESS_GENERAL);
        prefixes.put("necro", IMappingElement.LESS_GENERAL);
        prefixes.put("neo", IMappingElement.LESS_GENERAL);
        prefixes.put("non", IMappingElement.DISJOINT);//   1. A prefix used in the sense of not to negate the meaning of the word to which it is attached, as in nonattention (or non-attention), nonconformity, nonmetallic and nonsuit.
        prefixes.put("nona", IMappingElement.LESS_GENERAL);
        prefixes.put("oct", IMappingElement.LESS_GENERAL);
        prefixes.put("octa", IMappingElement.LESS_GENERAL);
        prefixes.put("olig", IMappingElement.LESS_GENERAL);
        prefixes.put("oligo", IMappingElement.LESS_GENERAL);
        prefixes.put("omni", IMappingElement.LESS_GENERAL);
        prefixes.put("ortho", IMappingElement.LESS_GENERAL);
        prefixes.put("out", IMappingElement.LESS_GENERAL);
        prefixes.put("over", IMappingElement.LESS_GENERAL);
        prefixes.put("ovi", IMappingElement.LESS_GENERAL);
        prefixes.put("palaeo", IMappingElement.LESS_GENERAL);
        prefixes.put("paleo", IMappingElement.LESS_GENERAL);
        prefixes.put("para", IMappingElement.LESS_GENERAL);
        prefixes.put("pebi", IMappingElement.LESS_GENERAL);
        prefixes.put("pent", IMappingElement.LESS_GENERAL);
        prefixes.put("penta", IMappingElement.LESS_GENERAL);
        prefixes.put("peta", IMappingElement.LESS_GENERAL);
        prefixes.put("phono", IMappingElement.LESS_GENERAL);
        prefixes.put("photo", IMappingElement.LESS_GENERAL);
        prefixes.put("pico", IMappingElement.LESS_GENERAL);
        prefixes.put("poly", IMappingElement.LESS_GENERAL);
        prefixes.put("praeter", IMappingElement.LESS_GENERAL);
        prefixes.put("pre", IMappingElement.LESS_GENERAL);
        prefixes.put("preter", IMappingElement.LESS_GENERAL);
        prefixes.put("proto", IMappingElement.LESS_GENERAL);
        prefixes.put("pseud", IMappingElement.LESS_GENERAL);
        prefixes.put("pseudo", IMappingElement.LESS_GENERAL);
        prefixes.put("psycho", IMappingElement.LESS_GENERAL);
        prefixes.put("ptero", IMappingElement.LESS_GENERAL);
        prefixes.put("pyro", IMappingElement.LESS_GENERAL);
        prefixes.put("quadr", IMappingElement.LESS_GENERAL);
        prefixes.put("quadri", IMappingElement.LESS_GENERAL);
        prefixes.put("quin", IMappingElement.LESS_GENERAL);
        prefixes.put("quinqu", IMappingElement.LESS_GENERAL);
        prefixes.put("quinque", IMappingElement.LESS_GENERAL);
        prefixes.put("radio", IMappingElement.LESS_GENERAL);
        prefixes.put("re", IMappingElement.EQUIVALENCE);//Added to a noun or verb to make a new noun or verb being made again or done again (sometimes implying an undoing first, as "reintegrate"); as renew, revisit, remake etc.
        prefixes.put("robo", IMappingElement.LESS_GENERAL);
        prefixes.put("schizo", IMappingElement.LESS_GENERAL);
        prefixes.put("semi", IMappingElement.LESS_GENERAL);
        prefixes.put("sept", IMappingElement.LESS_GENERAL);
        prefixes.put("septa", IMappingElement.LESS_GENERAL);
        prefixes.put("septem", IMappingElement.LESS_GENERAL);
        prefixes.put("septi", IMappingElement.LESS_GENERAL);
        prefixes.put("sex", IMappingElement.LESS_GENERAL);
        prefixes.put("sexa", IMappingElement.LESS_GENERAL);
        prefixes.put("sino", IMappingElement.LESS_GENERAL);//was Sino
        prefixes.put("step", IMappingElement.LESS_GENERAL);
        prefixes.put("sub", IMappingElement.LESS_GENERAL);
        prefixes.put("sui", IMappingElement.LESS_GENERAL);
        prefixes.put("super", IMappingElement.LESS_GENERAL);
        prefixes.put("supra", IMappingElement.LESS_GENERAL);
        prefixes.put("sym", IMappingElement.EQUIVALENCE);
        prefixes.put("syn", IMappingElement.EQUIVALENCE);
        prefixes.put("syl", IMappingElement.EQUIVALENCE);
        prefixes.put("tebi", IMappingElement.LESS_GENERAL);
        prefixes.put("tele", IMappingElement.EQUIVALENCE);
        prefixes.put("ter", IMappingElement.LESS_GENERAL);
        prefixes.put("tera", IMappingElement.LESS_GENERAL);
        prefixes.put("tetr", IMappingElement.LESS_GENERAL);
        prefixes.put("tetra", IMappingElement.LESS_GENERAL);
        prefixes.put("thermo", IMappingElement.LESS_GENERAL);
        prefixes.put("tri", IMappingElement.LESS_GENERAL);
        prefixes.put("ultra", IMappingElement.LESS_GENERAL);
        /*
From Old English un-, from Germanic, related to Latin in-
[edit] Prefix
un-
    * Added to adjectives, nouns and verbs, to give the following meanings:
   1. Not; denoting absence
          unannounced (not being announced)
          uneducated (not educated)
   2. (of nouns) a lack of
          unattractiveness (lack of attractiveness; ugliness)
          unrest (a lack of rest [i.e., peace]; war)
   3. Violative of; contrary to
          unconstitutional (in violation of or contrary to the constitution)
[edit] Usage notes
    * Some words formed in this way also have counterparts using in- or non-.
         */
        prefixes.put("un", IMappingElement.DISJOINT);
        prefixes.put("under", IMappingElement.LESS_GENERAL);
        prefixes.put("uni", IMappingElement.LESS_GENERAL);
        prefixes.put("up", IMappingElement.LESS_GENERAL);
        prefixes.put("ur", IMappingElement.LESS_GENERAL);
        prefixes.put("uro", IMappingElement.LESS_GENERAL);
        prefixes.put("vice", IMappingElement.LESS_GENERAL);
        prefixes.put("vid", IMappingElement.LESS_GENERAL);
        prefixes.put("xeno", IMappingElement.LESS_GENERAL);
        prefixes.put("xero", IMappingElement.LESS_GENERAL);
        prefixes.put("xylo", IMappingElement.LESS_GENERAL);
        prefixes.put("y", IMappingElement.LESS_GENERAL);
        prefixes.put("yocto", IMappingElement.LESS_GENERAL);
        prefixes.put("yotta", IMappingElement.LESS_GENERAL);
        prefixes.put("zepto", IMappingElement.LESS_GENERAL);
        prefixes.put("zetta", IMappingElement.LESS_GENERAL);
        prefixes.put("zo", IMappingElement.LESS_GENERAL);
        prefixes.put("zoo", IMappingElement.LESS_GENERAL);

        //"roots"
        prefixes.put("farm", IMappingElement.LESS_GENERAL);

        //"roots"
        //e.g. parrot-fish is less general than fish
        //or it is a fish? :)
        //to handle cases like almond-tree, apple-tree
        suffixes.put("fish", IMappingElement.LESS_GENERAL);
        suffixes.put("fish's", IMappingElement.LESS_GENERAL);
        suffixes.put("fishes", IMappingElement.LESS_GENERAL);
        suffixes.put("way", IMappingElement.LESS_GENERAL);
        suffixes.put("ways", IMappingElement.LESS_GENERAL);
        suffixes.put("bird", IMappingElement.LESS_GENERAL);
        suffixes.put("bird's", IMappingElement.LESS_GENERAL);
        suffixes.put("birds", IMappingElement.LESS_GENERAL);
        suffixes.put("room", IMappingElement.LESS_GENERAL);
        suffixes.put("rooms", IMappingElement.LESS_GENERAL);
        suffixes.put("grass", IMappingElement.LESS_GENERAL);
        suffixes.put("grasses", IMappingElement.LESS_GENERAL);
        suffixes.put("boat", IMappingElement.LESS_GENERAL);
        suffixes.put("boats", IMappingElement.LESS_GENERAL);
        suffixes.put("bush", IMappingElement.LESS_GENERAL);
        suffixes.put("bushes", IMappingElement.LESS_GENERAL);
        suffixes.put("bone", IMappingElement.LESS_GENERAL);
        suffixes.put("bones", IMappingElement.LESS_GENERAL);
        suffixes.put("band", IMappingElement.LESS_GENERAL);
        suffixes.put("bands", IMappingElement.LESS_GENERAL);
        suffixes.put("cake", IMappingElement.LESS_GENERAL);
        suffixes.put("cakes", IMappingElement.LESS_GENERAL);
        suffixes.put("shop", IMappingElement.LESS_GENERAL);
        suffixes.put("shops", IMappingElement.LESS_GENERAL);
        suffixes.put("mill", IMappingElement.LESS_GENERAL);
        suffixes.put("mills", IMappingElement.LESS_GENERAL);
        suffixes.put("paper", IMappingElement.LESS_GENERAL);
        suffixes.put("papers", IMappingElement.LESS_GENERAL);
        suffixes.put("worship", IMappingElement.LESS_GENERAL);
        suffixes.put("snake", IMappingElement.LESS_GENERAL);
        suffixes.put("snake's", IMappingElement.LESS_GENERAL);
        suffixes.put("snakes", IMappingElement.LESS_GENERAL);
        suffixes.put("road", IMappingElement.LESS_GENERAL);
        suffixes.put("roads", IMappingElement.LESS_GENERAL);
        suffixes.put("hound", IMappingElement.LESS_GENERAL);
        suffixes.put("hound's", IMappingElement.LESS_GENERAL);
        suffixes.put("hounds", IMappingElement.LESS_GENERAL);
        suffixes.put("care", IMappingElement.LESS_GENERAL);
        suffixes.put("cares", IMappingElement.LESS_GENERAL);
        suffixes.put("virus", IMappingElement.LESS_GENERAL);
        suffixes.put("virus'", IMappingElement.LESS_GENERAL);
        suffixes.put("viruses", IMappingElement.LESS_GENERAL);
        suffixes.put("storm", IMappingElement.LESS_GENERAL);
        suffixes.put("storms", IMappingElement.LESS_GENERAL);
        suffixes.put("sail", IMappingElement.LESS_GENERAL);
        suffixes.put("sail's", IMappingElement.LESS_GENERAL);
        suffixes.put("sails", IMappingElement.LESS_GENERAL);
        suffixes.put("boot", IMappingElement.LESS_GENERAL);
        suffixes.put("boots", IMappingElement.LESS_GENERAL);
        suffixes.put("bee", IMappingElement.LESS_GENERAL);
        suffixes.put("bee's", IMappingElement.LESS_GENERAL);
        suffixes.put("bees", IMappingElement.LESS_GENERAL);
        suffixes.put("ache", IMappingElement.LESS_GENERAL);
        suffixes.put("aches", IMappingElement.LESS_GENERAL);
        suffixes.put("wear", IMappingElement.LESS_GENERAL);
        suffixes.put("wears", IMappingElement.LESS_GENERAL);
        suffixes.put("tit", IMappingElement.LESS_GENERAL);
        suffixes.put("tits", IMappingElement.LESS_GENERAL);
        suffixes.put("tax", IMappingElement.LESS_GENERAL);
        suffixes.put("tree", IMappingElement.LESS_GENERAL);
        suffixes.put("trees", IMappingElement.LESS_GENERAL);
        suffixes.put("taxes", IMappingElement.LESS_GENERAL);
        suffixes.put("spoon", IMappingElement.LESS_GENERAL);
        suffixes.put("spoons", IMappingElement.LESS_GENERAL);
        suffixes.put("song", IMappingElement.LESS_GENERAL);
        suffixes.put("songs", IMappingElement.LESS_GENERAL);
        suffixes.put("builder", IMappingElement.LESS_GENERAL);
        suffixes.put("builder's", IMappingElement.LESS_GENERAL);
        suffixes.put("builders", IMappingElement.LESS_GENERAL);
        suffixes.put("vine", IMappingElement.LESS_GENERAL);
        suffixes.put("vines", IMappingElement.LESS_GENERAL);
        suffixes.put("saddle", IMappingElement.LESS_GENERAL);
        suffixes.put("saddles", IMappingElement.LESS_GENERAL);
        suffixes.put("plant", IMappingElement.LESS_GENERAL);
        suffixes.put("plants", IMappingElement.LESS_GENERAL);
        suffixes.put("knife", IMappingElement.LESS_GENERAL);
        suffixes.put("knives", IMappingElement.LESS_GENERAL);
        suffixes.put("frog", IMappingElement.LESS_GENERAL);
        suffixes.put("frog's", IMappingElement.LESS_GENERAL);
        suffixes.put("frogs", IMappingElement.LESS_GENERAL);
        suffixes.put("chop", IMappingElement.LESS_GENERAL);
        suffixes.put("chops", IMappingElement.LESS_GENERAL);
        suffixes.put("writer", IMappingElement.LESS_GENERAL);
        suffixes.put("writer's", IMappingElement.LESS_GENERAL);
        suffixes.put("writers", IMappingElement.LESS_GENERAL);
        suffixes.put("wright", IMappingElement.LESS_GENERAL);
        suffixes.put("wrights", IMappingElement.LESS_GENERAL);
        suffixes.put("person", IMappingElement.LESS_GENERAL);
        suffixes.put("person's", IMappingElement.LESS_GENERAL);
        suffixes.put("persons", IMappingElement.LESS_GENERAL);
        suffixes.put("owner", IMappingElement.LESS_GENERAL);
        suffixes.put("owner's", IMappingElement.LESS_GENERAL);
        suffixes.put("owners", IMappingElement.LESS_GENERAL);
        suffixes.put("mint", IMappingElement.LESS_GENERAL);
        suffixes.put("rack", IMappingElement.LESS_GENERAL);
        suffixes.put("racks", IMappingElement.LESS_GENERAL);
        suffixes.put("name", IMappingElement.LESS_GENERAL);
        suffixes.put("names", IMappingElement.LESS_GENERAL);
        suffixes.put("mast", IMappingElement.LESS_GENERAL);
        suffixes.put("in-law", IMappingElement.LESS_GENERAL);
        suffixes.put("fruit", IMappingElement.LESS_GENERAL);
        suffixes.put("fruits", IMappingElement.LESS_GENERAL);
        suffixes.put("pox", IMappingElement.LESS_GENERAL);
        suffixes.put("poxes", IMappingElement.LESS_GENERAL);
        suffixes.put("hide", IMappingElement.LESS_GENERAL);
        suffixes.put("force", IMappingElement.LESS_GENERAL);
        suffixes.put("forces", IMappingElement.LESS_GENERAL);
    }

    public char match(String str1, String str2) {
        char rel;

        if (str1 == null || str2 == null) {
            rel = IMappingElement.IDK;
        } else {
            if ((str1.length() > 3) && (str2.length() > 3)) {
                if (str1.equals(str2)) {
                    rel = IMappingElement.EQUIVALENCE;
                } else if (str1.endsWith(str2)) {
                    rel = matchSuffix(str1, str2);
                } else if (str2.endsWith(str1)) {
                    rel = match(str2, str1);
                    rel = reverseRelation(rel);
                } else {
                    rel = IMappingElement.IDK;
                }
            } else {
                rel = IMappingElement.IDK;
            }//if ((str1.length() > 3) && (str2.length() > 3)) {
        }//null

        return rel;
    }

    /**
     * Computes the relation with suffix matcher.
     *
     * @param str1 the source input
     * @param str2 the target input
     * @return synonym, more general, less general or IDK relation
     */
    private char matchSuffix(String str1, String str2) {
        //here always str1.endsWith(str2)
        char rel = IMappingElement.IDK;
        int spacePos1 = str1.lastIndexOf(' ');
        String prefix = str1.substring(0, str1.length() - str2.length());
        if (-1 < spacePos1 && !prefixes.containsKey(prefix)) {//check prefixes - ordered set!unordered set
            if (str1.length() == spacePos1 + str2.length() + 1) {//adhesive tape<tape   attention deficit disorder<disorder
                rel = IMappingElement.LESS_GENERAL;
            } else {//connective tissue<issue
                String left = str1.substring(spacePos1 + 1, str1.length());
                char secondRel = match(left, str2);
                if (IMappingElement.MORE_GENERAL == secondRel ||
                        IMappingElement.EQUIVALENCE == secondRel) {
                    rel = IMappingElement.LESS_GENERAL;
                } else { //?,<,!
                    rel = secondRel;
                }
            }
        } else {
            if (prefix.startsWith("-")) {
                prefix = prefix.substring(1);
            }
            if (prefix.endsWith("-") && !prefixes.containsKey(prefix = prefix.substring(0, prefix.length() - 1))) {
                //prefix = prefix.substring(0, prefix.length() - 1);
                //smth like cajun-creole, parrot-fish
                //but anti-virus
                rel = IMappingElement.LESS_GENERAL;
            } else {
                if (prefixes.containsKey(prefix)) {
                    rel = prefixes.get(prefix);
                } else if (suffixes.containsKey(str2)) {
                    rel = suffixes.get(str2);
                }
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