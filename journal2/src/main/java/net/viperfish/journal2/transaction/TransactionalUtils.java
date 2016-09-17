package net.viperfish.journal2.transaction;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.viperfish.journal2.core.TransactionExecutor;
import net.viperfish.journal2.core.TransactionWithResult;

@Component
class TransactionalUtils {

	@Autowired
	private TransactionExecutor executor;

	<T> T transactionToResult(TransactionWithResult<T> trans) throws ExecutionException {
		try {
			return executor.call(trans).get();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

}
