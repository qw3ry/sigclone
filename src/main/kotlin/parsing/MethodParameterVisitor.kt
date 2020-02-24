package parsing

import Java9BaseVisitor
import Java9Parser
import parsing.data.ParameterBuilder
import help.combine
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.tree.TerminalNode
import java.lang.IllegalStateException

class MethodParameterVisitor : Java9BaseVisitor<ParameterBuilder>() {
  override fun defaultResult() = ParameterBuilder()

  override fun aggregateResult(aggregate: ParameterBuilder, nextResult: ParameterBuilder) =
      aggregate
          .setName(combine(aggregate.name, nextResult.name) { a, b -> a + b })
          .setType(combine(aggregate.type, nextResult.type) { a, b -> a + b })

  override fun visitUnannType(ctx: Java9Parser.UnannTypeContext) =
      defaultResult().setType(getTokens(ctx).filter { it.type == Java9Lexer.Identifier }.joinToString { it.text })

  override fun visitVariableDeclaratorId(ctx: Java9Parser.VariableDeclaratorIdContext) =
      defaultResult().setName(ctx.text)

  private fun getTokens(ctx: ParserRuleContext): List<Token> {
    return ctx.children.flatMap {
      when (it) {
        is TerminalNode -> listOf(it.symbol)
        is ParserRuleContext -> getTokens(it)
        else -> throw IllegalStateException()
      }
    }
  }
}