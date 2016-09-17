package net.viperfish.journal2.core;

import java.util.concurrent.Future;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

@Service("asyncTransactionExecutor")
class AsyncTransactionExecutor implements TransactionExecutor {

	public AsyncTransactionExecutor() {
		// TODO Auto-generated constructor stub
	}

	@Async
	@Override
	public void run(Transaction trans) {
		trans.execute();
	}

	@Async
	@Override
	public <T> Future<T> call(TransactionWithResult<T> trans) {
		trans.execute();
		return new AsyncResult<T>(trans.getResult());
	}

}
