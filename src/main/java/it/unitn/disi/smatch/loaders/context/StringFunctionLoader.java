package it.unitn.disi.smatch.loaders.context;

import it.unitn.disi.smatch.data.trees.Context;
import it.unitn.disi.smatch.data.trees.IContext;
import it.unitn.disi.smatch.data.trees.INode;
import it.unitn.disi.smatch.loaders.ILoader;

import java.util.ArrayList;
import java.util.List;


/**
 * Loads a function from a string into a tree structure.
 * The string to be converted in the form: fn(arg,arg,..), where arg can be fn(arg,..)
 *
 * @author Juan Pane pane@disi.unitn.it
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class StringFunctionLoader extends BaseContextLoader<IContext, INode> implements IContextLoader {

    private final static char COMMA = ',';
    private final static char OPEN_PARENTHESIS = '(';
    private final static char CLOSE_PARENTHESIS = ')';

    private final static String DESCRIPTION = "String with a function";

    /**
     * Converts the string with a function into a tree.
     *
     * @param location a single line with the string in the format: fn(arg,arg,..), where arg can be fn(arg,..)
     * @return tree representation of the function
     */
    public IContext loadContext(String location) throws ContextLoaderException {
        IContext result = new Context();
        parse(location, result, null);
        createIds(result);
        return result;
    }

    public String getDescription() {
        return DESCRIPTION;
    }

    public ILoader.LoaderType getType() {
        return ILoader.LoaderType.STRING;
    }

    /**
     * Parses the string to get the children of the given node.
     *
     * @param inString string to be parsed
     * @param context  a tree being created
     * @param parent   the parent node of the parsed tokens
     */
    static void parse(String inString, IContext context, INode parent) {
        List<String> tokens = getCommaTokens(inString);
        for (String token : tokens) {
            int isFunction = token.indexOf(OPEN_PARENTHESIS);
            if (isFunction >= 0) {
                String funcName = token.substring(0, isFunction);
                INode newParent = addChild(parent, context, funcName);
                String arguments = token.substring(isFunction + 1, token.length() - 1);
                parse(arguments, context, newParent);
            } else {
                addChild(parent, context, token);
            }
        }
    }

    /**
     * Adds a node to the tree.
     *
     * @param parent  parent node, if this is null then we are creating the root of the tree
     *                and the child parameter represents the name of the root
     * @param context a tree being created
     * @param child   child to be added
     * @return inserted node
     */
    static INode addChild(INode parent, IContext context, String child) {
        INode result;
        if (null == parent) { //if this is true, then it is the root of the tree
            result = context.createRoot(child);
        } else {
            result = parent.createChild(child);
        }

        return result;
    }

    /**
     * Tokenizes the string into siblings.
     *
     * @param inString string to be parsed, in the form arg,arg,.. where arg can be fn(arg,arg,..)
     * @return vector of siblings
     */
    static List<String> getCommaTokens(String inString) {
        String input = inString.trim();
        List<String> tokens = new ArrayList<>();
        String token;
        while (0 < input.length()) {
            if (COMMA == input.charAt(0)) {
                input = input.substring(1);
            }
            token = getNextCommaToken(input);

            input = input.substring(token.length());
            tokens.add(token.trim());
        }

        return tokens;

    }

    /**
     * Computes the first argument from a list of arguments
     *
     * @param inString arg,arg,... arg can also be fn(arg,..)
     * @return the first argument of the given string
     */
    static String getNextCommaToken(String inString) {
        String token = "";

        //number of open parenthesis
        int parenthesis = 0;

        for (int i = 0; i < inString.length(); i++) {

            if (OPEN_PARENTHESIS == inString.charAt(i)) {
                parenthesis++;
                token += inString.charAt(i);

            } else if (CLOSE_PARENTHESIS == inString.charAt(i)) {
                parenthesis--;
                token += inString.charAt(i);
            } else if (COMMA == inString.charAt(i)) {
                //if there is no open parenthesis finish
                if (parenthesis == 0) {
                    break;
                } else {
                    token += inString.charAt(i);
                }
            } else {
                token += inString.charAt(i);
            }

        }

        return token;

    }
}
