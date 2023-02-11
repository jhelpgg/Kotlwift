package fr.jhelp.kotlwift

// @Try somethingMyFail()
// =>
// try somethingMyFail()
// ---
// @Try x = somethingMyFail()
// =>
// x = try somethingMyFail()
@Target(AnnotationTarget.EXPRESSION, AnnotationTarget.LOCAL_VARIABLE)
@Retention(AnnotationRetention.SOURCE)
annotation class Try

@Target(AnnotationTarget.EXPRESSION, AnnotationTarget.LOCAL_VARIABLE)
@Retention(AnnotationRetention.SOURCE)
annotation class Super

// @Override
// =>
// override
@Target(AnnotationTarget.CONSTRUCTOR)
@Retention(AnnotationRetention.SOURCE)
annotation class Override

// @ImportSwift("Library")
// =>
// import Library
annotation class ImportSwift(val name: String)

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class TestCaseClass

// @Extension("String")
//
// fun String.myMethod() {}
//
// =>
// public extension String
// {
//    func myMethod() {}
// }
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Extension(val className: String)

// fun doSomething(@Escaping block : () -> Unit)
// =>
// func doSomething(block : @escaping () -> Unit)
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class Escaping

@Target(AnnotationTarget.EXPRESSION, AnnotationTarget.LOCAL_VARIABLE)
@Retention(AnnotationRetention.SOURCE)
annotation class WeakSelf(val returnValue:String)

//  private @Weak var somethingNullable? : Type = null
// OR
//  @Weak private var somethingNullable? : Type = null
// ->
// private weak var somethingNullable? : Type = null
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
annotation class Weak()


