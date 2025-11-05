import { Button } from "./ui/button";
import { Loader2 } from "lucide-react";

export default function LoadingButton() {
  return (
    <Button>
      <Loader2 className="mr-2 size-4 animate-spin" />
      Loading
    </Button>
  );
}
