export function Debounce(func: (...args: any) => any, delay: number) {
  let timer: NodeJS.Timeout | null = null;

  return (...args: any) => {
    if (timer != null) clearTimeout(timer);

    timer = setTimeout(() => {
      func(...args);
      if (timer) clearTimeout(timer);
    }, delay);
  };
}
