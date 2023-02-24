package xyz.kandrac.reflection

import javassist.bytecode.ClassFile
import org.reflections.Reflections
import org.reflections.scanners.Scanner
import org.reflections.util.ConfigurationBuilder
import org.reflections.util.FilterBuilder
import org.reflections.util.JavassistHelper
import org.reflections.util.QueryBuilder
import xyz.kandrac.game.configuration
import java.lang.reflect.Method

val defaultPackageName get() = configuration.packageName

fun getMethodsWithName(methodName: String, packageName: String = defaultPackageName): Set<Method> {
    val reflections = Reflections(
        ConfigurationBuilder()
            .forPackage(packageName)
            .filterInputsBy(FilterBuilder().includePackage(packageName))
            .setScanners(MethodNameScanner())
    )
    return reflections[MethodNameScanner().with(methodName).`as`(Method::class.java)]
}

fun getMethod(functionName: String, returnType: Class<*>, vararg expectedArgs: Class<*>): Method? {
    return getMethodsWithName(functionName)
        .firstOrNull { it.parameterCount == expectedArgs.size && it.returnType == returnType }
}

fun getClassesWithName(className: String, packageName: String = defaultPackageName): Set<Class<*>> {
    val reflections = Reflections(
        ConfigurationBuilder()
            .forPackage(packageName)
            .filterInputsBy(FilterBuilder().includePackage(packageName))
            .setScanners(ClassNameScanner())
    )
    return reflections[ClassNameScanner().with(className).asClass<Class<*>>()]
}

private open class MethodNameScanner : Scanner, QueryBuilder {
    override fun scan(classFile: ClassFile?): List<MutableMap.MutableEntry<String, String>> =
        JavassistHelper.getMethods(classFile).toList().mapNotNull { method ->
            entry(method.name, JavassistHelper.methodName(classFile, method))
        }

    override fun index() = MethodNameScanner::class.simpleName
}

private open class ClassNameScanner : Scanner, QueryBuilder {
    override fun scan(classFile: ClassFile): List<MutableMap.MutableEntry<String, String>> =
        listOf(entry(classFile.name.substringAfterLast('.'), classFile.name))

    override fun index() = MethodNameScanner::class.simpleName
}
