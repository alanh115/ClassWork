# Java TAC to MIPS Transliteration

Once you have rewritten your JAVA method into the JAVA TAC style, you can use the information in this document to transliterate your method into a MIPS subroutine.

Note that sometimes there is not a 100% direct correlation between your JAVA TAC instruction and a set of MIPS instruction. Hence, some thinking is necessary when performing the transliteration.

## Other Resources

  - MIPS Cheat-sheet: ~/classes/comp122/reference/MIPS-cheatsheet.pdf
  - Java to Java TAC transformation: (under development)

## Notation:
  - Scalar values:                lower case letters, e.g., `a, b, c, .. z`
  - Arrays and Memory locations:  upper case letters, e.g., `A, B, C, .. Z`
  - Literals and Immediate values:  `imm`, e.g., 1, 34, 'a'
  - Registers:  a word prefix with a dollar, e.g., $zero
  - Metasymbol: a word enclosed in braces, e.g., {loop}

### Mathematical Equations 

  * Notation: TAC Instructions and Corresponding MIPS Instructions
    - Patterns
      1. Java Instructions with one immediate value:  `x = a <op_i> imm;`
         - `MIPS <op_i>  <- Java operator`
           * addi: +, subi: -, ori: |, andi: &, xori: ^
      1. Java Instructions with two variables: `x = a <op> b;`
         - `MIPS <op>  <- Java operator`
           * add: +, sub: -, or: |, and: &, xor: ^,


      | TAC Subroutine                | MIPS Instruction          |
      |-------------------------------|---------------------------|
      | `return a;`                   | `move $v0, a`             |
      |                               | `jr $ra`                  |
      | `return imm;`                 | `li $v0, imm`             |
      |                               | `jr $ra`                  |

      | TAC Equations                 | MIPS Instructions         |
      |-------------------------------|---------------------------|
      | `;`                           | `nop`                     |
      | `x = imm;`                    | `li x, imm`               |
      | `x = - imm;`                  | `subi x, $zero, imm`      |
      | `x = - a;`                    | `sub  x, $zero, a`        |
      | `x = a;`                      | `move x, a`               |
      | `x = a <op_i> imm; `          | `<op_i> x, a, imm`        |
      | `x = a <op> b;`               | `<op> x, a, b`            |
      | `x = ~ a;`                    | `nor x, a, $zero`         |
      | `x = a >>> imm;`              | `srl x, a, imm`           |
      | `x = a >> imm;`               | `sra x, a, imm`           |
      | `x = a << imm;`               | `sll x, a, imm`           |
      | `x = a >>> b;`                | `srlv x, a, b`            |
      | `x = a >> b;`                 | `srav x, a, b`            |
      | `x = a << b;`                 | `sllv x, a, b`            |

      | TAC Boolean                   | MIPS Instructions         |
      |-------------------------------|---------------------------|
      | `x = true;`                   | `li x, 1`                 |
      | `x = false;`                  | `li x, 0`                 |
      | `x = a == b;`                 | `seq x, a, b`             |
      | `x = a == imm;`               | `seq x, a, imm`           |
      | `x = a <cond> b;`             | `s<cond> x, a, b`         |
      | `x = a <cond> imm;`           | `s<cond> x, a, imm`       |
      | `x = a && b;`                 | `and x, a, b`             |
      | `x = a && imm;`               | `imm x, a, b`             |
      | `x = a || b;`                 | `or x, a, b`              |
      | `x = a || imm;`               | `ori x, a, imm`           |
      | `x = ! a;`                    | `xor x, a, -2`            |

      [^bool See the Conditional Mapping section for <cond> values]

      | TAC Mult / Div Equations      | MIPS Instruction          |
      |-------------------------------|---------------------------|
      | `x = a * b;`                  | `mult a, b`               |
      |                               | `mflo x`                  |
      |                               |                           |
      | `x = a / b;`                  | `div a, b`                |
      |                               | `mflo x`                  |
      |                               |                           |
      | `x = a % b;`                  | `div a, b`                |
      |                               | `mfhi x`                  |
      |                               |                           |

      | TAC Mult/Div Pseudo Equations | MIPS Instruction          |
      |-------------------------------|---------------------------|
      | `x = a * b;`                  | `mul x, a, b`             |
      | `x = a / b;`                  | `div x, a, b`             |
      | `x = a % b;`                  | `rem x, a, b`             |


### Array Operations with non-Java Pointer Operations

 1. For static arrays

  | TAC Array Equations           | MIPS Instruction          |
  |-------------------------------|---------------------------|
  |                               |                           |
  | `x = A[v];`                   | `lbu x A(v)`              |
  |                               |                           |
  | `A[v] = x;`                   | `sb x, A(v)`              |


  | TAC Array Equations           | MIPS Instruction          |
  |-------------------------------|---------------------------|
  |                               |                           |
  | `x = A[imm];`                 | `la p, A`                 |
  |                               | `lbu x, imm(p)`           |
  |                               |                           |
  |                               |                           |
  | `x = A[v];`                   | `la p, A`                 |
  |                               | `add p, p, v`             |
  |                               | `lbu x, 0(p)`             |
  |                               |                           |
  | `A[imm] = x;`                 | `la p, A`                 |
  |                               | `sb x, imm(p)`            |
  |                               |                           |
  | `A[v] = x;`                   | `la p, A`                 |
  |                               | `add p, p, v`             |
  |                               | `sb x, 0(p)`              |

  1. For registers arrays:
     - the address of A is already located in a register
     - &A don't the register that contains the address of A

  | TAC Array Equations           | MIPS Instruction          |
  |-------------------------------|---------------------------|
  |                               |                           |
  | `x = A[imm];`                 | `move p, &A`              |
  |                               | `lbu x, imm(p)`           |
  |                               |                           |
  |                               |                           |
  | `x = A[v];`                   | `move p, &A`              |
  |                               | `add p, p, v`             |
  |                               | `lbu x, 0(p)`             |
  |                               |                           |
  | `A[imm] = x;`                 | `move p, &A`              |
  |                               | `sb x, imm(p)`            |
  |                               |                           |
  | `A[v] = x;`                   | `move p, &A`              |
  |                               | `add p, p, v`             |
  |                               | `sb x, 0(p)`              |


  | C TAC Array Equations         | MIPS Instruction          |
  |-------------------------------|---------------------------|
  |                               |                           |
  | `p = & A;`                    | `la p, A`                 |
  | `x = (* p);`                  | `lbu x, 0(p)`             |
  | `(* p) = x;`                  | `sb x, 0(p)`              |
  |   
  [^ C TAC: These instruction can be used to do pointer walking.]


### Comparison Mappings: (`<comp>` --> `<! comp>`)

  | `TAC <comp>` | `MIPS <comp>` | `MIPS <! comp>` |`TAC <! comp>` |
  |:------------:|:-------------:|:---------------:|:-------------:|
  | `<`          | `lt`          | `ge`            |  `>=`         |
  | `<=`         | `le`          | `gt`            |  `>`          |
  | `!=`         | `ne`          | `eq`            |  `==`         |
  | `==`         | `eq`          | `ne`            |  `!=`         |
  | `>=`         | `ge`          | `lt`            |  `<`          |
  | `>`          | `gt`          | `le`            |  `<=`         |
   


### Loop Statements: 
  * Reference labels
     * {init}: the block of code that is executed prior to loop
     * {loop}: the line of code containing the keyword for the loop: do, while
     * {body}: the body of a loop
     * {done}: the statement after the loop


  | TAC Control Flow                  | MIPS Instruction           |
  |-----------------------------------|----------------------------|
  | `label: ;`                        | `label: nop`               |
  | `continue {loop};`                | `b {loop`                  |
  | `break {loop};`                   | `b {done}`                 |
  |                                   |                            |
  | `for(; a <comp> b ;) {`           | `b<! comp> a, b, {done}`   |
  | `while(a <comp> b) {`             | `b<! comp> a, b, {done}`   |
  |                                   |                            |
  | `for(; a <comp> b ;) {`           | `b<comp> a, b, {body}`     |
  |                                   | `b {done}`                 |
  |                                   |                            |
  | `do {`                            | `nop`                      |
  | `} while ( a <comp> b );`         | `b<comp> a, b, {loop}`     |


### Condition Statments:  If, If_else, and the If_else-if_else

  * Reference labels
     * {init}:   the implicit first line of code prior to the conditional statement.
     * {cond}:   the line of code that contains: `if (a <cond> b)`.
     * {next_0}: the consequence code block for the conditional statement.
     * {next_n}: a sequence of code blocks after the next_0 block: next_1, ..., next_n .
     * {done}:   the statement after conditional statment.
     
  | TAC Control Flow                   | MIPS Instruction             |
  |----------------------------------- |------------------------------|
  | `label: ;`                         | `label: nop`                 |
  | `break {comp};`                    | `b {done}`                   |
  |                                    |                              |
  | `if (a <comp> b) {`                | `b<! comp> a, b, {next_1}`   |
  | `else if ( a <comp b> ) {`         | `b<! comp> a, b, {next_n+1}` |
  |                                    |                              | 
  | `if (a <comp> b) {`                | `b<comp> a, b, {next_0}`     |
  |                                    | `b {next_1}`                 |
  |                                    |                              |
  | `else if (a <comp> b) {`           | `b<comp> a, b, {next_n}`     |
  |                                    | `b {next_n+1}`               |
  |                                    |                              | 
  | `else {`                           |                              |
  | `}`                                |                              |
  |                                    |                              |
  | `if (a <comp> b) break {cond};`    | `b<comp> a, b, {done}`       |
  | `if (a <comp> b) continue {cond};` | `b<comp> a, b, {cond}`       |
  |                                    |                              |



### Switch 

As part of the transliteration of a `switch` statement, each of the "case" labels need to be transformed into a proper label, e.g., 
  *  `case 'a':` --> `case_a:`
  *  `case  1 :` --> `case_1:`

 * Reference labels
     * {var}:  the introduced variable for controlling the switch statement
     * {imm}:  an immediate value
     * {char}: an ASCII character
     * {next}: refers to the next case block
     * {done}: the statement after the switch
       

  | TAC Control Flow          | MIPS Instructions                        |
  |---------------------------|------------------------------------------|
  | `switch ( {var} )`        | `nop`                                    |
  | `case {imm}: ;`           | `case_{imm}:   li $gp, {imm}`            |
  |                           |                                          |
  | `case '{char}': ;`        | `case_{char}:  li $gp, '{char}'`         |
  |                           |                                          |
  | `default:  ;`             | `default:      nop`                      |
  |                           |                                          |
  | `mips.next("{next}");`    | `bne {var}, $gp, case_{next}`            |
  | `mips.merge("{next}");`   | `b body_{next}`                          |
  |                           |                                          |
  | `mips.next("default");`   | `bne {var}, $gp, default    `            |
  | `mips.merge("default");`  | `b default    `                          |


  * Example
    ```
      $t1 = X + 2;
      stmt:  nop                         # switch ($t1) {
        case_1: li $gp, 1                #   case 1:
                bne $t1, $gp, case_2     #          mips.next("2");
                                         #          // code block
                b done                   #          break stmt;
                b body_2                 #          mips.merge("2");

        case_2: li $gp, 2                #   case 2:
                bne $t1, $gp, case_3     #          mips.next("3");
                                         #          // code block
                b done                   #          break stmt;
                b body_3                 #          mips.merge("3");


        case_3: li $gp, 3                #   case 3:
                bne $t1, $gp, default    #          mips.next("default");
                                         #          // code block
                b done                   #          break stmt;
                b default                #          mips.merge("default");

         default:                        #   default:
                                         #      // code block 
                b done
                                         # }  
    done:       nop                      # ; 
    ```
 


### Predefined Subroutines and Associated Macros
  1. Output Routines

     | Java TAC                      | MIPS Macro                |
     |-------------------------------|---------------------------|
     | `mips.print_d(a);`            | `print_d(a)`              |
     | `mips.print_di(imm);`         | `print_di(imm)`           |
     | ...                           |                           |

     * See [print_routines.md](./print_routines.md)

  1. Input Routines

     | Java TAC                      | MIPS Macro                |
     |-------------------------------|---------------------------|
     | `mips.read_d();`              | `read_d()`                |
     | `mips.read(fd, buff, imm);`   | `read(fd, buff, imm)`     |
     | ...                           | ...                       |
     | `X = mips.retval();`          | `move X, $v0`             |

     * See [read_routines.md](./read_routines.md)


  1. Memory associated Routines
     | `mips.sbrk(size);`            | `sbrk(size)`              |
     | `mips.sbrki(imm);`            | `sbrki(imm)`              |
     | `X = mips.retval();`          | `move X, $v0`             |


  1. MIPS Stack Routines

     | Java TAC                      | MIPS Macro                |
     |-------------------------------|---------------------------|
     | `mips.push(a);`               | `push a`                  |
     | `a = ({cast}) mips.pop();`    | `pop a`                   |




## Appendix

### "Continue" Statement
  If your original Java code contains a "continue" statement, the simplistic method used to perform the Java -> Java TAC  transformation needs to be augmented.

  Consider the following sample Java code:

  ```java

  for (i=0; i <=10; i++) {

    if ( a < b ) continue;
   
    a = pem + das;

    if ( a < b ) break;

  }
  ```

  Transforming the code in the simplistic method yields:

  ```java TAC
  init:     ;
            i = 0; 
     loop:  for (; i <=10 ;) {         
     body:    ;
              if ( a < b ) {
                 i++;
                 continue;   
               }

               a = pem + das;

               if ( a < b ) break;
      next:    ;                        
               i++;
               continue loop;
             }
  done:      ;
  ```

  Consider first if statement in the body section. The `continue` statement results in first line of the loop to executed next. But we need to execute the next section prior to returning to the top of the loop to keep the semantics of the original code.

  To address this issue, a copy of the 'next' block must be copied and placed just prior to the users "continue" statement. This transformation is depicted in the following code block.

  ```java TAC
  init:  {
            ;
            i = 0; 
          }
  loop:  for (; i <=10 ;) { 
  body:    {
              ;
              if ( a < b ) {
                { // copy of the next block
                  ;
                  i++;
                }
                continue loop;
              }
               
              a = pem + das;

              if ( a < b ) break;
            }
  next:     {
              ;
              i++;
            }
            continue loop;
          }
  done:  ;
  ```

  If you could use a "goto" in Java, a "continue" statement could be transformed into a "goto {next}".


### C Switch TAC --> MIPS, with a range
  * Consider the following macro:
    ```mips
    .macro position_in_range(%index, %low, %high)
      # returns index within the range
      # returns -1 if not in the range
      $v0 = -1;
      if (%index >= %low) {
        if (%index <= %high) 
          $v0 = %index - %low;
      }
      .end_macro
      ```

  * Consider the following C switch statement
    ```c
    switch (%input) {
      case '0'...'9' :   digit = digit - '0';
          break;

      case 'A'...'F'  :  digit = digit - 'A' 
                         digit = digit + 10;
                         break

       case 'a'...'f'  :  digit = digit - 'a' 
                          digit = digit + 10;
                          break;

       default:           digit = -1;
     }
     ```

   * Resulting MIPS code
     ```mips
     case_0_9:   index_in_range(%input, '0', '9')  
                 blt $v0, $zero, case_A_F
                    subi digit, digit, '0'
                 b done

     case_A_F:   index_in_range(%input, 'A', 'F') 
                 blt $v0, $zero, case_a_f
                   subi digit, digit, 'A'
                   addi digit, digit, 10  
                 b done   

     case_a_f:   index_in_range(%input, 'a', 'f') 
                 blt $v0, $zero, default
                   subi digit, digit, 'a'
                   addi digit, digit, 10
                 b done

     default:    li digit, -1
                 b done

     done:       nop    
     ```

   * Consider the semantics of the following switch statement
     ```c
     switch (%input) {
       case '0'...'9' :  
              break;

       case 'A' - 'F'  :  
              break

       case 1, 2, 3, 0 : 
              break;

       case 1 | 2 | 3 | 0 : 
              break;

       case str* :
              break;

       case func(arg) :
              break;

       case > 10 :
              break;

       default:           
              break;
     }
     ```

