package parsing

import Java9BaseVisitor
import Java9Parser
import parsing.data.MethodDeclaration
import java.io.File

class MethodExtractionVisitor(val file: File) : Java9BaseVisitor<MutableList<MethodDeclaration>>() {
  override fun defaultResult() = ArrayList<MethodDeclaration>()

  override fun aggregateResult(
      aggregate: MutableList<MethodDeclaration>,
      nextResult: MutableList<MethodDeclaration>
  ): MutableList<MethodDeclaration> {
    aggregate.addAll(nextResult)
    return aggregate
  }

  override fun visitNormalClassDeclaration(ctx: Java9Parser.NormalClassDeclarationContext): MutableList<MethodDeclaration> {
      return ClassVisitor(file, ctx.identifier().text).visitChildren(ctx)
  }

  override fun visitNormalInterfaceDeclaration(ctx: Java9Parser.NormalInterfaceDeclarationContext): MutableList<MethodDeclaration> {
    return ClassVisitor(file, ctx.identifier().text).visitChildren(ctx)
  }
}