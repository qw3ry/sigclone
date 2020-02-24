package parsing

import Java9BaseVisitor
import Java9Parser
import parsing.data.MethodDeclarationBuilder
import help.combine
import java.io.File

class MethodDeclarationVisitor(val file: File) : Java9BaseVisitor<MethodDeclarationBuilder>() {
  private var lookingForMethodId = false
  private val mpv = MethodParameterVisitor()

  override fun defaultResult() = MethodDeclarationBuilder(file)

  override fun aggregateResult(aggregate: MethodDeclarationBuilder, nextResult: MethodDeclarationBuilder) =
      aggregate
          .setName(combine(aggregate.name, nextResult.name) { a, b -> a + b })
          .setReturnType(combine(aggregate.returnType, nextResult.returnType) { a, b -> a + b })
          .setParameters { params -> params.addAll(nextResult.parameters) }
          .setLines(combine(aggregate.lines, nextResult.lines) { a, _ -> a })
          .setModifiers { it.addAll(nextResult.modifiers) }

  override fun visitMethodBody(ctx: Java9Parser.MethodBodyContext) = defaultResult()

  override fun visitResult(ctx: Java9Parser.ResultContext) = defaultResult().setReturnType(ctx.getTokens(Java9Lexer.Identifier).joinToString { it.text })

  override fun visitMethodModifier(ctx: Java9Parser.MethodModifierContext) = defaultResult().setModifiers { it.add(ctx.text) }

  override fun visitMethodDeclarator(ctx: Java9Parser.MethodDeclaratorContext): MethodDeclarationBuilder {
    lookingForMethodId = true
    return super.visitMethodDeclarator(ctx)
  }

  override fun visitIdentifier(ctx: Java9Parser.IdentifierContext) =
      if (lookingForMethodId) {
        lookingForMethodId = false
        defaultResult().setName(ctx.text)
      } else {
        defaultResult()
      }

  override fun visitDims(ctx: Java9Parser.DimsContext) = defaultResult().setReturnType(ctx.getTokens(Java9Lexer.Identifier).joinToString { it.text })

  override fun visitFormalParameter(ctx: Java9Parser.FormalParameterContext) =
      defaultResult().setParameters { l -> l.add(mpv.visitFormalParameter(ctx).build()) }

  // NOTE: varargs and arrays not recognized
  override fun visitLastFormalParameter(ctx: Java9Parser.LastFormalParameterContext): MethodDeclarationBuilder =
      defaultResult().setParameters { l -> l.add(mpv.visitLastFormalParameter(ctx).build()) }

  override fun visitMethodDeclaration(ctx: Java9Parser.MethodDeclarationContext): MethodDeclarationBuilder {
    return aggregateResult(
        super.visitMethodDeclaration(ctx),
        defaultResult().setLines(ctx.start.line, ctx.stop.line)
    )
  }
}