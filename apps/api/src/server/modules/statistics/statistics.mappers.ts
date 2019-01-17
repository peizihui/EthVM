import { Statistic } from 'ethvm-common'

const toStatistic = (statistic: any): Statistic => {
  const s: any = {
    date: statistic.date,
    value: statistic.bigInteger || statistic.int
  }
  return s
}

export { toStatistic }
