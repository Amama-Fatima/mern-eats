import { Request, Response, NextFunction } from "express";
import jwt, { JwtPayload } from "jsonwebtoken";

//this is to add the userId property to the Request interface of express
//we can write req.userId to access the userId property because of this
declare global {
  namespace Express {
    interface Request {
      userId: string;
    }
  }
}

const verifyToken = (req: Request, res: Response, next: NextFunction) => {
  const token = req.cookies["authCookie"];
  if (!token) {
    res.status(401).json({ message: "Unauthorized" });
    return;
  }

  try {
    const decoded = jwt.verify(token, process.env.JWT_SECRET as string);
    req.userId = (decoded as JwtPayload).userId; //userId is the always stored in the http cookie
    next();
  } catch (err) {
    console.log(err);
    res.status(401).json({ message: "Unauthorized" });
    return;
  }
};

export default verifyToken;
