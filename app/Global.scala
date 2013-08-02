import controllers.SearchController
import lucene.LuceneWrapper
import play.api._

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    Logger.info("Application has started")
    SearchController.doReindex()
    Logger.info("Index finished")
  }

  override def onStop(app: Application) {
    Logger.info("Application shutdown...")
  }

}
