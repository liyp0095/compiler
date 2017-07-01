/*
 * Copyright 2017, Robert Dyer, Kaushik Nimmala
 *                 and Bowling Green State University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package boa.types.shadow;

import boa.compiler.ast.Call;
import boa.compiler.ast.expressions.Expression;
import boa.compiler.ast.Factor;
import boa.compiler.ast.Identifier;
import boa.compiler.ast.Node;
import boa.compiler.SymbolTable;
import boa.compiler.transforms.ASTFactory;
import boa.types.BoaInt;
import boa.types.BoaProtoList;
import boa.types.BoaShadowType;
import boa.types.proto.enums.StatementKindProtoMap;
import boa.types.proto.ExpressionProtoTuple;
import boa.types.proto.StatementProtoTuple;


/**
 * A shadow type for TryStatement.
 * 
 * @author rdyer
 * @author kaushin
 */
public class TryStatementShadow extends BoaShadowType  {
    /**
     * Construct a {@link TryStatementShadow}.
     */
    public TryStatementShadow() {
        super(new StatementProtoTuple());

        
        addShadow("body", new BoaProtoList(new StatementProtoTuple()));
        addShadow("finallyblock", new StatementProtoTuple());
        addShadow("catchclauses", new StatementProtoTuple());
        addShadow("resources", new BoaProtoList(new ExpressionProtoTuple()));
    }

    /** {@inheritDoc} */
    @Override
	public Node lookupCodegen(final String name, final Factor node, final SymbolTable env) { 

        if ("catchclauses".equals(name)) {
            // TODO splice(${0}.statements, 1, ...)
           
            // ${0}.statements
            Expression tree =  ASTFactory.createSelector((Identifier)node.getOperand(), "statements", new BoaProtoList(new StatementProtoTuple()),new BoaProtoList(new StatementProtoTuple()), env);

            
            return ASTFactory.createCallFactor(node,"subList", env, new BoaProtoList(new StatementProtoTuple()), tree, ASTFactory.createIntLiteral(2), ASTFactory.createIntLiteral(-1));

            }

        if ("finallyblock".equals(name)) {
            // TODO len(${0}.statements) > 1 ? (${0}.statements[len(${0}.statements) - 1].kind == StatementKind.CATCH ? ${0}.statements[len(${0}.statements) - 1] : null) : null
            return null;
        }

        if ("body".equals(name)) {
            // ${0}.statements
            return ASTFactory.createSelector( "statements", new BoaProtoList(new StatementProtoTuple()),  env);
            
        }

        if ("resources".equals(name)) {
            // ${0}.initializations
            return ASTFactory.createSelector("initializations",  new BoaProtoList(new ExpressionProtoTuple()),   env);
            
            
        }


        throw new RuntimeException("invalid shadow field: " + name);
    }

    /** {@inheritDoc} */
    @Override
    public Expression getKindExpression(final SymbolTable env) {
        return getKindExpression("StatementKind", "TRY", new StatementKindProtoMap(), env);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "TryStatement";
    }
}
