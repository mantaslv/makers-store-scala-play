import org.openqa.selenium.{By, WebDriver}
import org.openqa.selenium.chrome.ChromeDriver
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import org.scalatestplus.selenium.WebBrowser

class SignUpSpec() extends PlaySpec with GuiceOneAppPerTest with WebBrowser {
  System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver")
  val driver: WebDriver = new ChromeDriver()

  "sign up page" should {
    "should have heading of Sign Up" in {
      driver.get("http://localhost:9000/signup")

      // driver.findElement(By.linkText("Sign Up")).click()
      val heading = driver.findElement(By.tagName("h1")).getText
      heading mustBe "Sign Up"
    }
  }
}
