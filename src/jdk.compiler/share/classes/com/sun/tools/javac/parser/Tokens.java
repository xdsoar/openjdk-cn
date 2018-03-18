/*
 * Copyright (c) 1999, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package com.sun.tools.javac.parser;

import java.util.Locale;

import com.sun.tools.javac.api.Formattable;
import com.sun.tools.javac.api.Messages;
import com.sun.tools.javac.parser.Tokens.Token.Tag;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Filter;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Names;

/** A class that defines codes/utilities for Java source tokens
 *  returned from lexical analysis.
 *
 *  <p><b>This is NOT part of any supported API.
 *  If you write code that depends on this, you do so at your own risk.
 *  This code and its internal interfaces are subject to change or
 *  deletion without notice.</b>
 */
public class Tokens {

    private final Names names;

    /**
     * Keyword array. Maps name indices to Token.
     */
    private final TokenKind[] key;

    /**  The number of the last entered keyword.
     */
    private int maxKey = 0;

    /** The names of all tokens.
     */
    private Name[] tokenName = new Name[TokenKind.values().length];

    public static final Context.Key<Tokens> tokensKey = new Context.Key<>();

    public static Tokens instance(Context context) {
        Tokens instance = context.get(tokensKey);
        if (instance == null)
            instance = new Tokens(context);
        return instance;
    }

    protected Tokens(Context context) {
        context.put(tokensKey, this);
        names = Names.instance(context);
        for (TokenKind t : TokenKind.values()) {
            if (t.name != null)
                enterKeyword(t.name, t);
            else
                tokenName[t.ordinal()] = null;
        }

        key = new TokenKind[maxKey+1];
        for (int i = 0; i <= maxKey; i++) key[i] = TokenKind.IDENTIFIER;
        for (TokenKind t : TokenKind.values()) {
            if (t.name != null)
            key[tokenName[t.ordinal()].getIndex()] = t;
        }
    }

    private void enterKeyword(String s, TokenKind token) {
        Name n = names.fromString(s);
        tokenName[token.ordinal()] = n;
        if (n.getIndex() > maxKey) maxKey = n.getIndex();
    }

    /**
     * Create a new token given a name; if the name corresponds to a token name,
     * a new token of the corresponding kind is returned; otherwise, an
     * identifier token is returned.
     */
    TokenKind lookupKind(Name name) {
        name = mapCNKeyWord(name);
        if (name.equals("\u5b57\u7b26\u4e32")) {
            System.out.println(name.getIndex());
        }
        return (name.getIndex() > maxKey) ? TokenKind.IDENTIFIER : key[name.getIndex()];
    }

    static String mapCNKeyWord(String name) {
        String s = name;
        switch (name) {
            case "\u62bd\u8c61": s = "abstract"; break;
            case "\u65ad\u8a00": s = "assert"; break;
            case "\u5e03\u5c14": s = "boolean"; break;
            case "\u4e2d\u65ad": s = "break"; break;
            case "\u5b57\u8282": s = "byte"; break;
            case "\u4e8b\u4f8b": s = "case"; break;
            case "\u6355\u83b7": s = "catch"; break;
            case "\u5b57\u7b26": s = "char"; break;
            case "\u7c7b": s = "class"; break;
            case "\u5e38\u91cf": s = "const"; break;
            case "\u7ee7\u7eed": s = "continue"; break;
            case "\u9ed8\u8ba4": s = "default"; break;
            case "\u6267\u884c": s = "do"; break;
            case "\u53cc\u7cbe\u5ea6\u6d6e\u70b9": s = "double"; break;
            case "\u5426\u5219": s = "else"; break;
            case "\u679a\u4e3e": s = "enum"; break;
            case "\u6269\u5c55": s = "extends"; break;
            case "\u7ec8\u7ed3": s = "final"; break;
            case "\u6700\u540e": s = "finally"; break;
            case "\u6d6e\u70b9": s = "float"; break;
            case "\u5bf9\u4e8e": s = "for"; break;
            case "\u8df3\u8f6c": s = "goto"; break;
            case "\u5982\u679c": s = "if"; break;
            case "\u5b9e\u73b0": s = "implements"; break;
            case "\u5bfc\u5165": s = "import"; break;
            case "\u662f\u5b9e\u4f8b": s = "instanceof"; break;
            case "\u6574\u578b": s = "int"; break;
            case "\u63a5\u53e3": s = "interface"; break;
            case "\u957f\u6574\u578b": s = "long"; break;
            case "\u672c\u5730": s = "native"; break;
            case "\u65b0\u5efa": s = "new"; break;
            case "\u5305": s = "package"; break;
            case "\u79c1\u6709": s = "private"; break;
            case "\u4fdd\u62a4": s = "protected"; break;
            case "\u516c\u5f00": s = "public"; break;
            case "\u8fd4\u56de": s = "return"; break;
            case "\u77ed\u6574\u578b": s = "short"; break;
            case "\u9759\u6001": s = "static"; break;
            case "\u4e25\u683c\u6d6e\u70b9": s = "strictfp"; break;
            case "\u8d85\u7c7b": s = "super"; break;
            case "\u5207\u6362": s = "switch"; break;
            case "\u540c\u6b65": s = "synchronized"; break;
            case "\u6b64\u5904": s = "this"; break;
            case "\u629b\u51fa": s = "throw"; break;
            case "\u629b\u51fa\u591a\u4e2a": s = "throws"; break;
            case "\u6682\u65f6": s = "transient"; break;
            case "\u5c1d\u8bd5": s = "try"; break;
            case "\u7a7a\u7f3a": s = "void"; break;
            case "\u539f\u5b50": s = "volatile"; break;
            case "\u5f53": s = "while"; break;
            case "\u771f": s = "true"; break;
            case "\u5047": s = "false"; break;
            case "\u7a7a\u503c": s = "null"; break;
            case "\u5b57\u7b26\u4e32": s = "String"; break;
            case "\u7cfb\u7edf": s = "System"; break;
            case "\u51fa": s = "out"; break;
            case "\u6253\u5370\u4e00\u884c": s = "println"; break;
            default:
                break;
        }
        return s;
    }

    static Name mapCNKeyWord(Name name) {
        String s = mapCNKeyWord(name.toString());
        return names.fromString(s);
    }


    TokenKind lookupKind(String name) {
        return lookupKind(mapCNKeyWord(name));
    }

    /**
     * This enum defines all tokens used by the javac scanner. A token is
     * optionally associated with a name.
     */
    public enum TokenKind implements Formattable, Filter<TokenKind> {
        EOF(),
        ERROR(),
        IDENTIFIER(Tag.NAMED),
        ABSTRACT("abstract"),
        ASSERT("assert", Tag.NAMED),
        BOOLEAN("boolean", Tag.NAMED),
        BREAK("break"),
        BYTE("byte", Tag.NAMED),
        CASE("case"),
        CATCH("catch"),
        CHAR("char", Tag.NAMED),
        CLASS("class"),
        CONST("const"),
        CONTINUE("continue"),
        DEFAULT("default"),
        DO("do"),
        DOUBLE("double", Tag.NAMED),
        ELSE("else"),
        ENUM("enum", Tag.NAMED),
        EXTENDS("extends"),
        FINAL("final"),
        FINALLY("finally"),
        FLOAT("float", Tag.NAMED),
        FOR("for"),
        GOTO("goto"),
        IF("if"),
        IMPLEMENTS("implements"),
        IMPORT("import"),
        INSTANCEOF("instanceof"),
        INT("int", Tag.NAMED),
        INTERFACE("interface"),
        LONG("long", Tag.NAMED),
        NATIVE("native"),
        NEW("new"),
        PACKAGE("package"),
        PRIVATE("private"),
        PROTECTED("protected"),
        PUBLIC("public"),
        RETURN("return"),
        SHORT("short", Tag.NAMED),
        STATIC("static"),
        STRICTFP("strictfp"),
        SUPER("super", Tag.NAMED),
        SWITCH("switch"),
        SYNCHRONIZED("synchronized"),
        THIS("this", Tag.NAMED),
        THROW("throw"),
        THROWS("throws"),
        TRANSIENT("transient"),
        TRY("try"),
        VOID("void", Tag.NAMED),
        VOLATILE("volatile"),
        WHILE("while"),
        INTLITERAL(Tag.NUMERIC),
        LONGLITERAL(Tag.NUMERIC),
        FLOATLITERAL(Tag.NUMERIC),
        DOUBLELITERAL(Tag.NUMERIC),
        CHARLITERAL(Tag.NUMERIC),
        STRINGLITERAL(Tag.STRING),
        TRUE("true", Tag.NAMED),
        FALSE("false", Tag.NAMED),
        NULL("null", Tag.NAMED),
        UNDERSCORE("_", Tag.NAMED),
        ARROW("->"),
        COLCOL("::"),
        LPAREN("("),
        RPAREN(")"),
        LBRACE("{"),
        RBRACE("}"),
        LBRACKET("["),
        RBRACKET("]"),
        SEMI(";"),
        COMMA(","),
        DOT("."),
        ELLIPSIS("..."),
        EQ("="),
        GT(">"),
        LT("<"),
        BANG("!"),
        TILDE("~"),
        QUES("?"),
        COLON(":"),
        EQEQ("=="),
        LTEQ("<="),
        GTEQ(">="),
        BANGEQ("!="),
        AMPAMP("&&"),
        BARBAR("||"),
        PLUSPLUS("++"),
        SUBSUB("--"),
        PLUS("+"),
        SUB("-"),
        STAR("*"),
        SLASH("/"),
        AMP("&"),
        BAR("|"),
        CARET("^"),
        PERCENT("%"),
        LTLT("<<"),
        GTGT(">>"),
        GTGTGT(">>>"),
        PLUSEQ("+="),
        SUBEQ("-="),
        STAREQ("*="),
        SLASHEQ("/="),
        AMPEQ("&="),
        BAREQ("|="),
        CARETEQ("^="),
        PERCENTEQ("%="),
        LTLTEQ("<<="),
        GTGTEQ(">>="),
        GTGTGTEQ(">>>="),
        MONKEYS_AT("@"),
        CUSTOM;

        public final String name;
        public final Tag tag;

        TokenKind() {
            this(null, Tag.DEFAULT);
        }

        TokenKind(String name) {
            this(name, Tag.DEFAULT);
        }

        TokenKind(Tag tag) {
            this(null, tag);
        }

        TokenKind(String name, Tag tag) {
            this.name = name;
            this.tag = tag;
        }

        public String toString() {
            switch (this) {
            case IDENTIFIER:
                return "token.identifier";
            case CHARLITERAL:
                return "token.character";
            case STRINGLITERAL:
                return "token.string";
            case INTLITERAL:
                return "token.integer";
            case LONGLITERAL:
                return "token.long-integer";
            case FLOATLITERAL:
                return "token.float";
            case DOUBLELITERAL:
                return "token.double";
            case ERROR:
                return "token.bad-symbol";
            case EOF:
                return "token.end-of-input";
            case DOT: case COMMA: case SEMI: case LPAREN: case RPAREN:
            case LBRACKET: case RBRACKET: case LBRACE: case RBRACE:
                return "'" + name + "'";
            default:
                return name;
            }
        }

        public String getKind() {
            return "Token";
        }

        public String toString(Locale locale, Messages messages) {
            return name != null ? toString() : messages.getLocalizedString(locale, "compiler.misc." + toString());
        }

        @Override
        public boolean accepts(TokenKind that) {
            return this == that;
        }
    }

    public interface Comment {

        enum CommentStyle {
            LINE,
            BLOCK,
            JAVADOC,
        }

        String getText();
        int getSourcePos(int index);
        CommentStyle getStyle();
        boolean isDeprecated();
    }

    /**
     * This is the class representing a javac token. Each token has several fields
     * that are set by the javac lexer (i.e. start/end position, string value, etc).
     */
    public static class Token {

        /** tags constants **/
        enum Tag {
            DEFAULT,
            NAMED,
            STRING,
            NUMERIC
        }

        /** The token kind */
        public final TokenKind kind;

        /** The start position of this token */
        public final int pos;

        /** The end position of this token */
        public final int endPos;

        /** Comment reader associated with this token */
        public final List<Comment> comments;

        Token(TokenKind kind, int pos, int endPos, List<Comment> comments) {
            this.kind = kind;
            this.pos = pos;
            this.endPos = endPos;
            this.comments = comments;
            checkKind();
        }

        Token[] split(Tokens tokens) {
            if (kind.name.length() < 2 || kind.tag != Tag.DEFAULT) {
                throw new AssertionError("Cant split" + kind);
            }

            TokenKind t1 = tokens.lookupKind(kind.name.substring(0, 1));
            TokenKind t2 = tokens.lookupKind(kind.name.substring(1));

            if (t1 == null || t2 == null) {
                throw new AssertionError("Cant split - bad subtokens");
            }
            return new Token[] {
                new Token(t1, pos, pos + t1.name.length(), comments),
                new Token(t2, pos + t1.name.length(), endPos, null)
            };
        }

        protected void checkKind() {
            if (kind.tag != Tag.DEFAULT) {
                throw new AssertionError("Bad token kind - expected " + Tag.STRING);
            }
        }

        public Name name() {
            throw new UnsupportedOperationException();
        }

        public String stringVal() {
            throw new UnsupportedOperationException();
        }

        public int radix() {
            throw new UnsupportedOperationException();
        }

        /**
         * Preserve classic semantics - if multiple javadocs are found on the token
         * the last one is returned
         */
        public Comment comment(Comment.CommentStyle style) {
            List<Comment> comments = getComments(Comment.CommentStyle.JAVADOC);
            return comments.isEmpty() ?
                    null :
                    comments.head;
        }

        /**
         * Preserve classic semantics - deprecated should be set if at least one
         * javadoc comment attached to this token contains the '@deprecated' string
         */
        public boolean deprecatedFlag() {
            for (Comment c : getComments(Comment.CommentStyle.JAVADOC)) {
                if (c.isDeprecated()) {
                    return true;
                }
            }
            return false;
        }

        private List<Comment> getComments(Comment.CommentStyle style) {
            if (comments == null) {
                return List.nil();
            } else {
                ListBuffer<Comment> buf = new ListBuffer<>();
                for (Comment c : comments) {
                    if (c.getStyle() == style) {
                        buf.add(c);
                    }
                }
                return buf.toList();
            }
        }
    }

    final static class NamedToken extends Token {
        /** The name of this token */
        public final Name name;

        public NamedToken(TokenKind kind, int pos, int endPos, Name name, List<Comment> comments) {
            super(kind, pos, endPos, comments);
            this.name = Tokens.mapCNKeyWord(name);
        }

        protected void checkKind() {
            if (kind.tag != Tag.NAMED) {
                throw new AssertionError("Bad token kind - expected " + Tag.NAMED);
            }
        }

        @Override
        public Name name() {
            return name;
        }
    }

    static class StringToken extends Token {
        /** The string value of this token */
        public final String stringVal;

        public StringToken(TokenKind kind, int pos, int endPos, String stringVal, List<Comment> comments) {
            super(kind, pos, endPos, comments);
            this.stringVal = stringVal;
        }

        protected void checkKind() {
            if (kind.tag != Tag.STRING) {
                throw new AssertionError("Bad token kind - expected " + Tag.STRING);
            }
        }

        @Override
        public String stringVal() {
            return stringVal;
        }
    }

    final static class NumericToken extends StringToken {
        /** The 'radix' value of this token */
        public final int radix;

        public NumericToken(TokenKind kind, int pos, int endPos, String stringVal, int radix, List<Comment> comments) {
            super(kind, pos, endPos, stringVal, comments);
            this.radix = radix;
        }

        protected void checkKind() {
            if (kind.tag != Tag.NUMERIC) {
                throw new AssertionError("Bad token kind - expected " + Tag.NUMERIC);
            }
        }

        @Override
        public int radix() {
            return radix;
        }
    }

    public static final Token DUMMY =
                new Token(TokenKind.ERROR, 0, 0, null);
}
