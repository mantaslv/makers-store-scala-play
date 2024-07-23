import org.openqa.selenium.{By, WebDriver}
import org.openqa.selenium.chrome.ChromeDriver
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.play.PlaySpec

/*class SignUpSpec extends PlaySpec with Matchers {
  System.setProperty("webdriver.chrome.driver", "/opt/homebrew/bin/chromedriver")
  val driver: WebDriver = new ChromeDriver()

  "sign up page" should "have heading of Sign Up" in {
    driver.get("http://localhost:9000/login")

    driver.findElement(By.linkText("Sign Up")).click()
    val heading = driver.findElement(By.tagName("h1")).getText
    heading shouldEqual "Sign Up"
  }
}*/
