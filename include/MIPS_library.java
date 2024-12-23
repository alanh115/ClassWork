import java.util.Scanner;
import java.io.*; 
import java.lang.Math;


//import java.util.*; 
//import java.util.regex.Pattern; 

// Sections of code:
//   1. MACHINE information
//   1. STACK support
//   1. TAC transformation support
//   1. SYSCALL support
//   1. IO support

class MIPS_library {

  // MACHINE
  final static int $zero = 0;
  static int $v0 = -1; 
  static int $v1 = -1; 
  static int $gp = -1;
  static int $fp = -1;
  static int $sp = -1;
  static int $ra = -1;

  // MEMORY
  static int sbrk_p = 0;
  static byte[] MEM = new byte[1024];
  static Object[] STACK = new Object[256];



  // TAC
  static public void next(String x) {
    return;
  }

  public static int retval() {
    return $v0;
  }




  // Potpourri: Things I thought I might need but just cruft at the moment

  //  TYPE Conversion
  public static int u_byte(byte value) {
    return Byte.toUnsignedInt(value);
  }
  public static int s_byte(byte value) {
    return value;
  }

  public static int u_half(short value) {
    return Short.toUnsignedInt(value);
  }
  public static int s_half(short value) {
    return value;
  }



// File: syscalls.j
//
// Description: a companion file for syscalls.s
//
// Purpose: to provide the corresponding macros via Java methods
//
//
//# | Macro Name       | Code | Prototype             |
//# |------------------|------|-----------------------|
//# | print_d          |  1   | void ƛ(int);          |
//# | print_di         |  1   | void ƛ(imm);          |
//# | print.s          |  2   | void ƛ(float);        | # to match c1 instructions
//# | print.d          |  3   | void ƛ(double);       | # to match c1 instructions
//# | print_f          |  3   | void ƛ(double);       | # to match printf
//# | print_s          |  4   | void ƛ(&str);         |
//# | print_si         |  4   | void ƛ(label);        |
//# | read_d           |  5   | int  ƛ(void);         |
//# | read_s           |  8   | void ƛ(&str, int);    |
//# | read_si          |  8   | void ƛ(&str, imm);    |
//# | sbrk (allocate)  |  9   | &buffer ƛ(int);       |
//# | sbrki (allocate) |  9   | &buffer ƛ(imm);       |
//# | exit (w/o value) | 10   | void exit(void);      |
//# | print_c          | 11   | void ƛ(byte);         |
//# | print_ci         | 11   | void ƛ(byte);         |
//# | read_c           | 12   | byte ƛ(void);         |
//# | open (fd)        | 13   | fd ƛ(&str, int, int); |
//# | read (from fd)   | 14   | int ƛ(fd, &buf, int); |
//# | write (to fd)    | 15   | int ƛ(fd, &buf, int); |
//# | close (fd)       | 16   | void ƛ(fd);           |
//# | exit (w value)   | 17   | void ƛ(int);          | 
//# | exiti (w value)  | 17   | void ƛ(imm);          |
//# | print_x          | 34   | void ƛ(int);          |
//# | print_xi         | 34   | void ƛ(imm);          |
//# | print_t          | 35   | void ƛ(imm);          |
//# | print_ti         | 35   | void ƛ(int);          |
//# | print_u          | 36   | void ƛ(int);          |
//# | print_ui         | 36   | void ƛ(imm);          |
//
// Refer to the definition of specific macros for additional information

  static Scanner stdin = new Scanner(System.in);

//# ####################################################
//# Macros that perform input from stdin
//#
//# | read_d           |  5   | int  ƛ(void);         |
//# | read_c           | 12   | byte ƛ(void);         |
//# | read_s           |  8   | void ƛ(&str, int);    |
//# | read_si          |  8   | void ƛ(&str, int);    |


  public static void read_d() {
    $v0 = stdin.nextInt();
  }


  public static void read_c() {
    String str = stdin.findInLine(".");

    if (str == null) {
      $v0 = '\0';
    }
    else {
      $v0 = str.charAt(0);
    }
  }

  // Should follow the semantics of UNIX 'fgets'.  
  //   Reads at most n-1 characters. A newline ('\n') is placed in the last
  //   character read, and then string is then padded with a null character ('\0').
  //   If n = 1, input is ignored, and a null byte written to the buffer.
  //   If n <=1, input is ignored, and nothing is written to the buffer.
  // $v0 defines the actual number of bytes read
  public static void read_s(char [] A, int count) {
    String str;
    char [] temp;

    str = stdin.nextLine() + "\n";
    temp = str.toCharArray();

    for (int i=0; i< str.length(); i++) {
       A[i] = temp[i];
    }

    // Does not return a value, hence $v0 == $v0
    $v0 = $v0;
  }
  public static void read_si(char [] A, int count) {
    read_s(A, count);
  }


//#####################################################
//# Macros that perform output to stdout
//#
//# | print_d          |  1   | void ƛ(int);          |
//# | print_di         |  1   | void ƛ(int);          |
//# | print.s          |  2   | void ƛ(float);        | # to match c1 instructions
//# | print.d          |  3   | void ƛ(double);       | # to match c1 instructions
//# | print_f          |  3   | void ƛ(double);       | # to match printf
//# | print_s          |  4   | void ƛ(&str);         |
//# | print_si         |  4   | void ƛ(label);        |
//# | print_c          | 11   | void ƛ(byte);         |
//# | print_ci         | 11   | void ƛ(byte);         |
//# | print_x          | 34   | void ƛ(int);          |
//# | print_xi         | 34   | void ƛ(int);          |
//# | print_t          | 35   | void ƛ(int);          | 
//# | print_ti         | 35   | void ƛ(int);          | 
//# | print_u          | 36   | void ƛ(int);          | 
//# | print_ui         | 36   | void ƛ(int);          | 

  public static void print_d(int register) {
    System.out.printf("%d", register);
    return;
  }

  public static void print_di(int immediate) {
    System.out.printf("%d", immediate);
    return;
  }

  //public static void print.s(float register) {
  //  System.out.printf("%f", register);
  //}
  //public static void print.d(double register) {
  //  System.out.printf("%f", register);
  //}
  public static void print_f(double register) {
    System.out.printf("%f", register);
  }

  public static void print_c(char register) {
    System.out.printf("%c", register);
    return;
  }
  public static void print_ci(char immediate) {
    System.out.printf("%c", immediate);
    return;
  }

  public static void print_s(String register) {
    System.out.printf("%s", register);
    return;
  }
  public static void print_si(String immediate) {
    System.out.printf("%s", immediate);
  }

  public static void print_s(char [] register) {
    // The char [] has a 

    int index;
    for (index=0; index < register.length; index++ ) {
       if (register[index] == '\0' ) break;
       System.out.printf("%c", register[index]);
     }
    return;
  }
  public static void print_si(char [] immediate) {
    print_s(immediate);
  }


  public static void print_x(int register) {
    System.out.printf("0x%08x", register);
    return;
  }
  public static void print_xi(int immediate  ) {
    System.out.printf("0x%08x", immediate);
    return;
  }

  // public static void print_t(int register) {
  //   StringBuilder binaryValue = new StringBuilder();
  //   String binaryString;
  //   long value = Integer.toUnsignedLong(register);  // Java does not have unsigned
  //   long remainder;
  //
  //   while (value > 0) {
  //       remainder = value % 2;
  //       value     = value / 2;
  //       binaryValue.append(remainder);
  //   }
  //   binaryString = binaryValue.reverse().toString();
  //   for (int i=32; i > binaryString.length(); i--) {
  //     System.out.printf("%c", '0');
  //   }
  //   System.out.printf("%s", binaryString);
  //   return;
  // }
  public static void print_t(int register) {
     print_bits(register, 31, 0);
     return;
  }
  public static void print_ti(int immediate) {
    print_bits(immediate, 31, 0);
    return;
  }


  public static void print_u(int register) {
    System.out.printf("%u", register);
  }
  public static void print_ui(int immediate) {
    System.out.printf("%u", immediate);
  }


// ######################################################
// # Macros that perform I/O on files
// # | open (fd)         | 13   | fd ƛ(&str, int, int); | filename, flags, mode
// # | read (from fd)    | 14   | int ƛ(fd, &buf, int); | bytes read |
// # | write (to fd)     | 15   | int ƛ(fd, &buf, int); | bytes read |
// # | close (fd)        | 16   | void ƛ(fd);           |

// # Open: Service 13: MARS implements three flag values: 
//#     0: read-only, 
//#     1: write-only with create
//#     9: write-only with create and append.  
//#     It ignores mode.  
//#     The returned file descriptor will be negative if the operation failed. 
//#  

  static Object FD_LIST[] = new Object[8];
  static int last_fd = 2;

  public static int open(String name, int flags, int mode) {

    last_fd ++;

    try {
      switch (flags) {
      case 0:
         FD_LIST[last_fd] = (Object) new FileInputStream(name);
         break;
      case 1:
      case 9:
         FD_LIST[last_fd] = (Object) new FileOutputStream(name);
         break;
       default:
          System.out.printf("Flag not implemented\n");
          System.exit(1);
          break;
      }
    } catch (IOException x) {
      return -1;
    }
    return last_fd;
  }

 public static int open(char [] name, int flags, int mode) {

    String string = new String(name);
    return open(string, flags, mode);
  }

  public static int read(int fd, byte buffer, int size) {
    try { 
      return ((FileInputStream) FD_LIST[fd]).read(MEM, buffer, size);
    } catch (IOException x) {
      return -1;
    }
  }

  public static int read(int fd, byte buffer[], int size) {
    try { 
      return ((FileInputStream) FD_LIST[fd]).read(buffer, 0, size);
    } catch (IOException x) {
      return -1;
    }
  }


  public static int write(int fd, byte buffer, int size) {
    try {
      ((FileOutputStream) FD_LIST[fd]).write(MEM, buffer, size);
    } catch (IOException x) {
      return -1;
    }
    return size;
   }


  public static int write(int fd, byte buffer[], int size) {
    try {
      ((FileOutputStream) FD_LIST[fd]).write(buffer, 0, size);
    } catch (IOException x) {
      return -1;
    }
    return size;
  }        

  public static void close(int fd) {
    try { 
      if (FD_LIST[fd] instanceof FileInputStream) 
        ( (FileInputStream) FD_LIST[fd]).close();
      else
         ( (FileOutputStream) FD_LIST[fd]).close();
     } catch (IOException x) {
       ;
     }
     return;
  }

// ######################################################
// # Macros that perform other system related activities
// 
// # | sbrk (allocate)   |  9   | &buffer ƛ(int);       |
// # | sbrki (allocate)  |  9   | &buffer ƛ(imm);       |
// # | exit              | 10   | void ƛ(void);         |
// # | exit              | 17   | void ƛ(int);          |
// # | exiti             | 17   | void ƛ(imm);          |

  public static void sbrk(int size) {
    $v0 = sbrk_p;
    sbrk_p += size;
  }
  public static void allocate(int size) {
    sbrk(size);
  }
  public static void sbrki(int size) {
    sbrk(size);
  }
  public static void allocatei(int size) {
    sbrk(size);
  }

  public static void exit(int register) {
    System.exit(register);
    return;
  }
  public static void exiti(int immediate) {
    System.exit(immediate);
    return;
  }

  // stack.s
   public void push(int register) {
    $sp = $sp + 1;
    STACK[$sp] = (Object) register;
  }

  public void push(char[] register) {
    $sp = $sp + 1;
    STACK[$sp] = (Object) register;
  }
  
  public Object pop() {
    Object x = (Object) STACK[$sp];
    $sp = $sp - 1; 
    return x;
  }

  public static void alloca(int register) {
     $sp = $sp - alloca_adjust(register, 0x03);
  }
  public static void allocai(int value) {
      alloca(value);
  }

  public static int alloca_adjust(int register, int mask) {
    // Modify the value of register to ensure multiple of the mask

    int amount = 0;
    int temp = 0;

    amount = register;
    temp   = amount & mask;
    if (temp != 0) {
      // not a multiple of the mask
      temp = ~ mask;
      amount = register + mask;
      amount = amount + 1;
    }
    return register;
  }
// # File: io.s
// #
// # Description: This file contains an additional set of compound macros
// #   to perform I/O functions.
// #
// #   These macros ensure the associated "v" and "a" registers are preserved.
// # 
// # Read a number from stdin: read_x and read_t.  (read_d is provided via syscalls.s)
// #   - read_<type>()
// #
// # Print a value that is in a register  
// #   - print_<type>(%reg)                # (see syscalls.s)
// #   - print_null()
// #   - print_quote("a quoted string")
// #   - print_binary32(%reg)
// #   - print_bits(%reg, %start, %end)  # %start and %end are immediate
// #       * given a 32-bit register, print the bits in the range from %start to %end (%start >= %end)
// #       * 31 ... %start - %end ... 0
// #
// # Print the value with a prefixed with a final newline (\n)
// #   - println_<type>(%reg)
// #   - println_register(%name, %reg)
// #   - println_binary32(%reg)
// #
// # Print an array of values
// #   - println_<type>(%reg, %count)
// #   - println_binary32(%reg, %count)
// #
// #
// # Future: Print the value with a space between each %count digit
// #   - print_<type>(%reg, %null, %count)
// #   - print_ln<type>(%reg, %null, %count)
// 



  public static void read_x() {
    $v0 = stdin.nextInt(16);
    return;
  }

  public static void read_o() {
    $v0 = stdin.nextInt(8);
    return;
  }

  public static void read_t() {
    $v0 = stdin.nextInt(2);
    return;
  }


// ########################
  public static void  print_null() {
    return;
  }


  public static void print_quote(String str) {
    System.out.printf("%s", str);
    return;
  }

  public static void print_register(String name, int value) {
    print_quote(name);
    print_ci('\t');
    print_d(value);
    print_quote("\t0x ");
    print_x(value);
    print_quote("\t0b ");
    print_bits(value, 31, 28);
    print_ci(' ');
    print_bits(value, 27, 24);
    print_ci(' ');
    print_bits(value, 23, 20);
    print_ci(' ');
    print_bits(value, 19, 16);
    print_ci(' ');
    print_bits(value, 15, 12);
    print_ci(' ');
    print_bits(value, 11, 8);
    print_ci(' ');
    print_bits(value, 7,  4);
    print_ci(' ');
    print_bits(value, 3,  0);
    return;
  }


  public static void print_bits(int reg, int start, int end) {

    int value;
    int count;
    int counter;

    value = reg << (31 - start);
    count = start - end + 1; 

top: for(; count > 0 ;) {
      if (value >= 0) {
        print_ci('0');
      } else {
        print_ci('1');
      }
      value = value << 1;
      count --;
      continue top;
    }
    return;
  }


  public static void print_binary32(int reg) {
    print_quote("| ");
    print_bits(reg, 31, 31);
    print_quote(" | ");
    print_bits(reg, 30, 23);
    print_quote(" | ");
    print_bits(reg, 22, 0);
    print_quote(" |");
    return;
  }


// ##########################
  public static void println_null() {
    return;
  }

  public static void println_d(int value) {
    print_d(value);
    print_ci('\n');
    return;
  }

  public static void  println_di(int value) {
    print_di(value);
    print_ci('\n');
    return;
  }


  // public static void println.s(float reg) {
  //   print.s(reg);
  //   print_ci('\n');
  //   return;
  // }

  // public static void println.d(double reg) {
  //   print.d(%reg)
  //   print_ci('\n')
  //   return;
  // }

  public static void println_f(float reg) {
    print_f(reg);
    print_ci('\n');
    return;
  }

  public static void println_s(char[] register) {
    print_s(register);
    System.out.printf("\n");
    return;
  }


  public static void println_si(char[] label) {
    print_si(label);
    print_ci('\n');
    return;
  }

  public static void println_c(char reg) {
    print_c(reg);
    print_ci('\n');
    return;
  }

  public static void println_ci(char imm) {
    print_ci(imm);
    print_ci('\n');
    return;
  }


  public static void println_x(int reg) {
    print_x(reg);
    print_ci('\n');
    return;
  }

  public static void println_xi(int imm) {
    print_xi(imm);
    print_ci('\n');
    return;
  }

  public static void println_t(int reg) {
    print_t(reg);
    print_ci('\n');
    return;
  }

  public static void println_ti(int imm) {
    print_ti(imm);
    print_ci('\n');
    return;
  }

  public static void println_u(int reg) {
    print_u(reg);
    print_ci('\n');
    return;
  }

  public static void println_ui(int imm) {
    print_ui(imm);
    print_ci('\n');
    return;
  }

  public static void println_binary32(int reg) {
    print_binary32(reg);
    print_ci('\n');
    return;
  }


  public static void println_register(String name, int reg) {
    print_register(name, reg);
    print_ci('\n');
    return;
  }



// # Array Macros
  public static void println_d(int arr[], int count) {
    int i;
    for (i=0; i < count; i++) {
      println_d(arr[i]);
      print_ci('\n');
    }
    return;
  }

  // public static void println.s(int arr[], int count) {
  //   int i;
  //   for (i=0; i < count; i++) {
  //     println.s(arr[i]);
  //     print_ci('\n');
  //   }
  //   return;
  // }

  // public static void println.d(int arr[], int count) {
  //   int i;
  //   for (i=0; i < count; i++) {
  //     println  (arr[i]);
  //     print_ci('\n');
  //   }
  //   return;
  // }

  public static void println_s(char arr[][], int count) {
    int i;
    for (i=0; i < count; i++) {
      println_s(arr[i]);
      print_ci('\n');
    }
    return;
  }

  public static void println_c(char arr[], int count) {
    int i;
    for (i=0; i < count; i++) {
      println_c(arr[i]);
      print_ci('\n');
    }
    return;
  }

  public static void println_x(int arr[], int count) {
    int i;
    for (i=0; i < count; i++) {
      println_x(arr[i]);
      print_ci('\n');
    }
    return;
  }

  public static void println_t(int arr[], int count) {
    int i;
    for (i=0; i < count; i++) {
      println_t(arr[i]);
      print_ci('\n');
    }
    return;
  }

  public static void println_u(int arr[], int count) {
    int i;
    for (i=0; i < count; i++) {
      println_u(arr[i]);
      print_ci('\n');
    }
    return;
  }

  public static void println_binary32(int arr[], int count) {
    int i;
    for (i=0; i < count; i++) {
      println_binary32(arr[i]);
      print_ci('\n');
    }
    return;
  }







}
