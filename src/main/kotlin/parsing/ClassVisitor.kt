package parsing

import Java9BaseVisitor
import Java9Parser
import parsing.data.MethodDeclaration
import parsing.data.Parameter
import java.io.File

class ClassVisitor(val file: File, vararg val classes: String) : Java9BaseVisitor<MutableList<MethodDeclaration>>() {

  private val methodDeclarationVisitor = MethodDeclarationVisitor(file)

  override fun defaultResult() = ArrayList<MethodDeclaration>()

  override fun aggregateResult(
      aggregate: MutableList<MethodDeclaration>,
      nextResult: MutableList<MethodDeclaration>
  ): MutableList<MethodDeclaration> {
    aggregate.addAll(nextResult)
    return aggregate
  }

  override fun visitMethodDeclaration(ctx: Java9Parser.MethodDeclarationContext): MutableList<MethodDeclaration>? {
    val methodBuilder = methodDeclarationVisitor.visit(ctx)
    if (!methodBuilder.modifiers.contains("static")) {
      classes.forEach { methodBuilder.parameters.add(Parameter(null, it)) }
    }
    return mutableListOf(methodBuilder.build())
  }

  override fun visitNormalClassDeclaration(ctx: Java9Parser.NormalClassDeclarationContext): MutableList<MethodDeclaration> {
    return if (ctx.classModifier().none { it?.STATIC() != null }) {
      ClassVisitor(file, *classes, ctx.identifier().text)
    } else {
      ClassVisitor(file, ctx.identifier().text)
    }.visitChildren(ctx)
  }

  override fun visitNormalInterfaceDeclaration(ctx: Java9Parser.NormalInterfaceDeclarationContext): MutableList<MethodDeclaration> {
    return if (ctx.interfaceModifier().none { it?.STATIC() != null }) {
      ClassVisitor(file, *classes, ctx.identifier().text)
    } else {
      ClassVisitor(file, ctx.identifier().text)
    }.visitChildren(ctx)
  }
}