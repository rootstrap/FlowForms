import SwiftUI
import shared

struct UI {

  private init() {
    /* struct just used for structure of the code */
  }
  
  enum CornerRadius {
    static let tiny: CGFloat = 2
    static let small: CGFloat = 5
    static let medium: CGFloat = 7
    static let large: CGFloat = 10
    static let huge: CGFloat = 20
  }
  
  enum Padding {
    static let tiny: CGFloat = 5
    static let small: CGFloat = 10
    static let medium: CGFloat = 20
    static let large: CGFloat = 30
    static let huge: CGFloat = 50
  }
  
  enum ScaleFactor {
    static let tiny: CGFloat = 0.3
    static let small: CGFloat = 0.5
    static let medium: CGFloat = 0.7
    static let large: CGFloat = 0.8
    static let huge: CGFloat = 0.9
  }
}

struct Animation {
  
  private init() {
    /* struct just used for structure of the code */
  }
  
  enum DirectionToAppear {
    case fromLeft
    case fromRight
    case fromTop
    case fromBottom
  }
  
  enum DirectionToDissapear {
    case toLeft
    case toRight
    case toTop
    case toBottom
  }
  
  enum Duration {
    static let veryShort: CGFloat = 0.3
    static let short: CGFloat = 0.5
    static let normal: CGFloat = 0.7
    static let long: CGFloat = 1
    static let veryLong: CGFloat = 1.2
  }
  
  enum Opacity {
    static let translucent: CGFloat = 0
    static let almostTranslucent: CGFloat = 0.3
    static let medium: CGFloat = 0.5
    static let almostOpaque: CGFloat = 0.8
    static let opaque: CGFloat = 1
  }
}
