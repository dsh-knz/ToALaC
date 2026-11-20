"""Практическая работа № 5, вариант 8, часть 2: рекурсивный спуск."""
import argparse
import re
import sys

# Время двух форматов (00:00..23:59 / 00.00..23.59),
# целые числа, переменные из 1-2 букв, знаки операций.
TOKEN_RE = re.compile(
    r'(?P<TIME>(?:[01][0-9]|2[0-3])[:.][0-5][0-9])'
    r'|(?P<INT>[0-9]+)'
    r'|(?P<ID>[A-Za-z]{1,2})'
    r'|(?P<OP>[=+*/;\[\]-])'
    r'|(?P<SPACE>\s+)'
)


class ParseError(Exception):
    pass


def tokenize(text):
    result = []
    pos = 0
    while pos < len(text):
        match = TOKEN_RE.match(text, pos)
        if match is None:
            raise ParseError('Недопустимый символ')
        kind = match.lastgroup
        if kind != 'SPACE':
            result.append(match.group() if kind == 'OP' else kind)
        pos = match.end()
    return result + ['$']


class Parser:
    def __init__(self, text):
        self.tokens = tokenize(text)
        self.pos = 0

    def look(self):
        return self.tokens[self.pos]

    def take(self, expected):
        if self.look() != expected:
            raise ParseError('Нарушение синтаксиса')
        self.pos += 1

    def program(self):
        self.expression()
        while self.look() == ';':
            self.take(';')
            self.expression()
        self.take('$')

    def expression(self):
        # Присваивание разрешено только при имени переменной слева.
        if self.look() == 'ID':
            self.take('ID')
            if self.look() == '=':
                self.take('=')
                self.expression()
                return
        else:
            self.unary()
        self.mul_tail()
        self.add_tail()

    def add_tail(self):
        while self.look() in ('+', '-'):
            self.take(self.look())
            self.term()

    def term(self):
        self.unary()
        self.mul_tail()

    def mul_tail(self):
        while self.look() in ('*', '/'):
            self.take(self.look())
            self.unary()

    def unary(self):
        if self.look() == '-':
            self.take('-')
            self.unary()
        else:
            self.factor()

    def factor(self):
        if self.look() in ('ID', 'INT', 'TIME'):
            self.take(self.look())
        elif self.look() == '[':
            self.take('[')
            self.expression()
            self.take(']')
        else:
            raise ParseError('Ожидается операнд')


def main():
    arg = argparse.ArgumentParser()
    arg.add_argument('-e', '--expression')
    args = arg.parse_args()
    source = args.expression if args.expression is not None else sys.stdin.read()
    try:
        Parser(source).program()
        print('Accepted')
    except (ParseError, RecursionError):
        print('Rejected')


if __name__ == '__main__':
    main()
