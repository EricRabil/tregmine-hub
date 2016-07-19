package info.tregmine.web;

import java.io.PrintWriter;
import java.util.Map;

import org.eclipse.jetty.server.Request;
import org.json.JSONException;
import org.json.JSONWriter;

import info.tregmine.Tregmine;
import info.tregmine.WebHandler;
import info.tregmine.database.IContextFactory;
import info.tregmine.database.db.DBContextFactory;
import info.tregmine.database.db.LoggingConnection;

public class QueryLogAction implements WebHandler.Action {
	public static class Factory implements WebHandler.ActionFactory {
		public Factory() {
		}

		@Override
		public WebHandler.Action createAction(Request request) {
			return new QueryLogAction();
		}

		@Override
		public String getName() {
			return "/querylog";
		}
	}

	private Map<String, LoggingConnection.LogEntry> log;

	public QueryLogAction() {
	}

	@Override
	public void generateResponse(PrintWriter writer) throws WebHandler.WebException {
		try {
			JSONWriter json = new JSONWriter(writer);
			json.array();
			for (LoggingConnection.LogEntry entry : log.values()) {
				json.object().key("sql").value(entry.sql).key("count").value(entry.invocationCount).key("avg")
						.value(entry.avgTime).key("max").value(entry.maxTime).endObject();
			}
			json.endArray();

			writer.close();
		} catch (JSONException e) {
			throw new WebHandler.WebException(e);
		}
	}

	@Override
	public void queryGameState(Tregmine tregmine) {
		IContextFactory ctxFactory = tregmine.getContextFactory();
		if (!(ctxFactory instanceof DBContextFactory)) {
			return;
		}

		this.log = ((DBContextFactory) ctxFactory).getLog();
	}
}
