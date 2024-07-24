import org.openqa.selenium.{By, WebDriver}
import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}
import org.openqa.selenium.support.ui.{ExpectedConditions, WebDriverWait}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerTest
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import play.api.Application
import play.api.db.{DBApi, Database}
import play.api.db.evolutions.Evolutions
import play.api.inject.guice.GuiceApplicationBuilder
import java.time.Duration

class SignUpSpec
  extends PlaySpec
    with GuiceOneServerPerTest
    with BeforeAndAfterEach
    with BeforeAndAfterAll {

  lazy val fakeApp: Application = new GuiceApplicationBuilder().build()
  lazy val database: Database = fakeApp.injector.instanceOf[DBApi].database("default")

  override def beforeEach(): Unit = {
    Evolutions.cleanupEvolutions(database)
    Evolutions.applyEvolutions(database)
  }

  System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver")
  val options = new ChromeOptions()
  options.addArguments("--headless")
  options.addArguments("--disable-gpu")
  options.addArguments("--window-size=1920,1080")

  val driver: WebDriver = new ChromeDriver(options)
  driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20))

  override def afterAll(): Unit =  driver.quit()

  "sign up page" should {
    "have heading of Sign Up" in {
      val baseUrl = s"http://localhost:$port"
      driver.get(baseUrl)
      driver.findElement(By.linkText("Sign Up")).click()

      val heading = driver.findElement(By.tagName("h1")).getText
      heading mustBe "Sign Up"
    }

    "sign up a user and take them to home page" in {
      val baseUrl = s"http://localhost:$port/signup"
      driver.get(baseUrl)

      driver.findElement(By.id("username")).sendKeys("testuser")
      driver.findElement(By.id("email")).sendKeys("test@example.com")
      driver.findElement(By.id("password")).sendKeys("Password$123")
      driver.findElement(By.id("submit")).click()

      val wait = new WebDriverWait(driver, Duration.ofSeconds(10))
      wait.until(ExpectedConditions.presenceOfElementLocated(By.id("index-heading")))

      val heading = driver.findElement(By.id("index-heading")).getText
      heading mustBe "Welcome to The Makers Store!"

      val dbApi = app.injector.instanceOf[DBApi]
      val connection = dbApi.database("default").getConnection()
      val statement = connection.prepareStatement("SELECT COUNT(*) FROM users WHERE username = ? AND email = ?")
      statement.setString(1, "testuser")
      statement.setString(2, "test@example.com")
      val resultSet = statement.executeQuery()
      resultSet.next()
      val userCount = resultSet.getInt(1)
      userCount mustBe 1
      connection.close()
    }
  }
}
