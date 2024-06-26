; ModuleID = '../../src/tests/ex1.ll'
declare i32 @printf(i8*, ...)
@print.str = constant [3 x i8] c"%d\00"

define i32 @main() {
  x = alloca i32, align 4
  store i32 0.0, i32* x, align 4
  ret i32 0
}
