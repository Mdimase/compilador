source_filename = "programa.ll"
target datalayout = "e-m:w-p270:32:32-p271:32:32-p272:64:64-i64:64-f80:128-n8:16:32:64-S128"
target triple = "x86_64-pc-windows-msvc19.29.30038"

declare i32 @puts(i8*)
declare i32 @printf(i8*, ...)
declare i32 @scanf(i8* %0, ...)

@.true = private constant[4 x i8] c".T.\00"
@.false = private constant[4 x i8] c".F.\00"
@.integer = private constant [3 x i8] c"%d\00"
@.float = private constant [3 x i8] c"%f\00"
@.integern = private constant [4 x i8] c"%d\0A\00"
@.floatn = private constant [4 x i8] c"%f\0A\00"
@int_read_format = unnamed_addr constant [3 x i8] c"%d\00"
@double_read_format = unnamed_addr constant [4 x i8] c"%lf\00"

@d = global i1 0
@hj = global i32 0
@rt = global float 0.0
@bv = global i1 0
@fghjl = global i1 0
@jk = global float 0.0
@df = global i32 0

define float @hola (i32 %t$8,float %t$9) {
  %t$10 = alloca i32 ; alloca = %t$10
  store i32 %t$8, i32* %t$10 ; %t$10 = %t$8
  %t$11 = alloca float ; alloca = %t$11
  store float %t$9, float* %t$11 ; %t$11 = %t$9
  %t$12 = load i32, i32* %t$10 ; %t$12 = hugo
  %t$13 = sitofp i32 %t$12 to float
  %t$14 = load float, float* %t$11 ; %t$14 = juan
  %t$15 = fadd float %t$13, %t$14
  ret float %t$15
}

define i32 @main(i32, i8**) {
  store i1 0, i1* @d ; @d = 0
  store i32 66, i32* @hj ; @hj = 66
  store float 5.559999942779541, float* @rt ; @rt = 5.559999942779541
  %t$1 = load float, float* @rt ; %t$1 = rt
  %t$2 = fcmp oeq float %t$1, 5.0
  store i1 %t$2, i1* @bv ; @bv = %t$2
  %t$3 = load i1, i1* @d ; %t$3 = d
  %t$4 = or i1 %t$3, 1
  store i1 %t$4, i1* @fghjl ; @fghjl = %t$4
  store float 0.0, float* @jk ; @jk = 0.0
  %t$5 = load float, float* @rt ; %t$5 = rt
  %t$6 = fadd float 1.0, %t$5
  %t$7 = fptosi float %t$6 to i32
  store i32 %t$7, i32* @df ; @df = %t$7
  %t$16 = alloca i32 ; alloca = %t$16
  store i32 50, i32* %t$16 ; %t$16 = 50
  %t$17 = alloca float ; alloca = %t$17
  %t$18 = fneg float 1.2300000190734863
  store float %t$18, float* %t$17 ; %t$17 = %t$18
  %t$19 = alloca i1 ; alloca = %t$19
  store i1 0, i1* %t$19 ; %t$19 = 0
  %t$20 = alloca i32 ; alloca = %t$20
  %t$21 = load i32, i32* %t$16 ; %t$21 = j
  %t$22 = add i32 %t$21, 500
  store i32 %t$22, i32* %t$20 ; %t$20 = %t$22
  %t$23 = load i32, i32* %t$20 ; %t$23 = int
  %t$24 = add i32 %t$23, 1
  store i32 %t$24, i32* %t$20 ; %t$20 = %t$24
  %t$26 = load i32, i32* @hj ; %t$26 = hj
  %t$27 = add i32 %t$26, 4
  %t$25 = call float @hola(i32 %t$27, float 1.5 )
  %t$28 = fpext float %t$25 to double
  %t$29 = call i32 (i8*, ...) @printf(i8* getelementptr([3 x i8], [3 x i8]* @.float, i32 0, i32 0), double %t$28)
ret i32 0
}
