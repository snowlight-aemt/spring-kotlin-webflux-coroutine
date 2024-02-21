##
## feature/feat/advanced
### 트레이스 식별자
coroutine 에서는 HTTP 요청 받는 Thread 와 처리를 하는 Worker Thread 가 나누어 있다.
또한 중단 후 이어서 처리하는 Worker Thread 가 반드시 동일하다고 보장할 수 없다 따라서, 처리가 
조금 까다롭다. 

`org.jetbrains.kotlinx:kotlinx-coroutines-slf4j` `withContext(MDCContext()) {}`
해당 라이브러리를 활용하여 동일하게 트레이스 식별자를 이어서 받을 수 있도록 수정. 

###
