package net.viperfish.journal2.transaction;

import java.util.concurrent.ExecutionException;

import net.viperfish.journal2.core.TransactionExecutor;
import net.viperfish.journal2.core.TransactionWithResult;

class TransactionalUtils {

	private TransactionExecutor executor;

	public TransactionalUtils(TransactionExecutor exe) {
		executor = exe;
	}

	<T> T transactionToResult(TransactionWithResult<T> trans) throws ExecutionException {
		try {
			return executor.call(trans).get();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

}
